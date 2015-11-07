package travel.snapshot.dp.qa.base;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;
import static org.joda.time.format.DateTimeFormat.forPattern;
import static travel.snapshot.dp.qa.base.TestUtils.dbHelper;

/**
 * Inserts data into the source data before tests are run,
 * remove them afterwards to restore database into the previous state.
 *
 * The "data" are defined via SQL INSERT statements stored in given external files.
 * Multiple INSERT statements have to be separated by semicolons.
 * Multiple values can be inserted via single INSERT statements.
 *
 * Basic placeholders in statements can be used:
 * {@code ${id}} - indicates that new UUID should be generated and used instead of placeholder
 * {@code ${now}} - indicates that current timestamp should be used instead of placeholder
 *
 * Typical usage in unit test - create field annotated by ClassRule annotation:
 * <pre>
 *   &#064;ClassRule
 *   public static InsertingDataRule dataRule = new InsertingDataRule("dimension_update.sql");
 * </pre>
 */
public class InsertingDataRule extends ExternalResource {

    private static final Logger logger = LoggerFactory.getLogger(InsertingDataRule.class);

    private static final String SQL_STATEMENTS_SEPARATOR = ";";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Pattern TABLE_NAME_PATTERN =
            // DOTALL flag is used to allow "." to match new line characters
            // so we can write sql insert statement on multiple lines
            Pattern.compile("\\s*INSERT INTO\\s+([a-zA-Z_-]+).*", Pattern.DOTALL);


    /** Enum of all placeholders supported in sql scripts. */
    private enum Placeholder {
        ID("\\$\\{id\\}"),
        NOW("\\$\\{now\\}");

        private final Pattern pattern;

        Placeholder(String pattern) {
            this.pattern = Pattern.compile(pattern);
        }

        Matcher matcher(String string) {
            return pattern.matcher(string);
        }
    }


    private final List<Path> sqlScriptsPaths = new ArrayList<>();
    private final Multimap<String, UUID> generatedIds = ArrayListMultimap.create();

    /**
     * Create rule reading INSERT statements from all given scripts and executing them.
     * @param sqlScriptsResourcePaths resource paths referencing the sql scripts - should contain
     *                                full resource path from the root "resources" directory
     */
    public InsertingDataRule(String... sqlScriptsResourcePaths) {
        notNull(sqlScriptsResourcePaths, "sqlScriptsResourcePaths cannot be null!");
        for (String sqlScriptPath : sqlScriptsResourcePaths) {
            this.sqlScriptsPaths.add(toPath(sqlScriptPath));
        }
    }

    @Override
    protected void before() throws Throwable {
        logger.info("action=insert_data status=start");
        for (Path scriptPath : sqlScriptsPaths) {
            final List<String> sqls = parseSqlStatementsFromFile(scriptPath);

            // use batch update since individual INSERTs are quite slow, even when inserting few rows
            dbHelper.sourceTemplate().batchUpdate(sqls.toArray(new String[sqls.size()]));
        }
        logger.info("action=insert_data status=finish");
    }

    @Override
    protected void after() {
        logger.info("action=delete_data status=start");
        deleteData();
        logger.info("action=delete_data status=finish");
    }

    private Path toPath(String sqlScriptPath) {
        notNull(sqlScriptPath, "sql script path cannot be null!");
        final URL sqlScriptResource = getClass().getClassLoader().getResource(sqlScriptPath);
        notNull(sqlScriptResource, "cannot find any resource denotated by path=" + sqlScriptPath);
        return Paths.get(sqlScriptResource.getPath());
    }

    private List<String> parseSqlStatementsFromFile(Path scriptPath) throws IOException {
        final String sqlScript = readFileToString(scriptPath.toFile());
        final String[] sqlStatements = sqlScript.split(SQL_STATEMENTS_SEPARATOR);

        final List<String> finalSqls = new ArrayList<>(sqlStatements.length);
        for (String sql : sqlStatements) {
            if (isNotBlank(sql)) {
                finalSqls.add(formatSql(sql));
            }
        }

        return finalSqls;
    }

    /**
     * Formats given sql statement by replacing all placeholders with final values.
     */
    private String formatSql(String sqlStatement) throws IOException {
        notEmpty(sqlStatement, "sqlStatement cannot be empty!");
        isTrue(containsIgnoreCase(sqlStatement, "INSERT INTO "),
                "INSERT statement expected, but found: " + sqlStatement);

        final String tableName = parseTableName(sqlStatement);

        final String sqlWithNowReplaced =
                replacePlaceholder(sqlStatement, nowReplacementGenerator(), Placeholder.NOW);
        return replacePlaceholder(sqlWithNowReplaced,
                idReplacementGenerator(tableName), Placeholder.ID);
    }

    private String parseTableName(String sqlStatement) {
        final Matcher matcher = TABLE_NAME_PATTERN.matcher(sqlStatement);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Cannot parse table name - invalid statement: " +
                    sqlStatement);
        }
    }

    private String replacePlaceholder(String sqlStatement, Supplier<String> replacementGenerator,
                                      Placeholder placeholder) {
        final StringBuffer sql = new StringBuffer();
        final Matcher idMatcher = placeholder.matcher(sqlStatement);
        while (idMatcher.find()) {
            // replacement is quoted - this works both for timestamps and UUIDs
            idMatcher.appendReplacement(sql, "'" + replacementGenerator.get() + "'");
        }
        idMatcher.appendTail(sql);
        return sql.toString();
    }

    private Supplier<String> idReplacementGenerator(final String tableName) {
        return () -> {
            final UUID newUuid = UUID.randomUUID();
            generatedIds.put(tableName, newUuid);
            return newUuid.toString();
        };
    }

    private Supplier<String> nowReplacementGenerator() {
        return () -> new DateTime(DateTimeZone.UTC).toString(TIMESTAMP_FORMAT);
    }

    private void deleteData() {
        // remove data inserted before to return database to the initial state
        final String[] deleteStatements = new String[generatedIds.size()];
        int i = 0;
        for (Map.Entry<String, UUID> tableRow : generatedIds.entries()) {
            deleteStatements[i] =
                    format("DELETE FROM %s WHERE ID='%s'", tableRow.getKey(), tableRow.getValue());
            i++;
        }
        dbHelper.sourceTemplate().batchUpdate(deleteStatements);
    }

}
