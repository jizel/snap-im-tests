package travel.snapshot.dp.qa.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.ConfigProps;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestUtils {

    public static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    private static final DbHelper dbHelper = new DbHelper();

    private TestUtils() {
        throw new AssertionError("Utility class - DO NOT INSTANTIATE!");
    }

    // TODO: consider renaming since it's used in TestDims as well.
    public static void testFactLoad(String sqlQueryForSource, String sqlQueryForTarget) throws Exception {
        final String outcomeSource = getQueryResultSource(sqlQueryForSource);
        final String outcomeTarget = getQueryResultTarget(sqlQueryForTarget);

        verifyOutcome(outcomeSource, outcomeTarget);
    }

    public static String withStartDate(String queryWithPlaceholder) {
        final String startDate = ConfigProps.getPropValue("etl.startDate");
        if (isEmpty(startDate)) {
            throw new IllegalStateException("etl.startDate property must be defined " +
                    "- check your configuration!");
        }
        // MSSQL requires the date to be quoted, otherwise we get empty result set
        return format(queryWithPlaceholder, "'" + startDate + "'");
    }

    private static String getQueryResultSource(String sqlQueryForSource) throws Exception {
        return dbHelper.sourceTemplate().queryForObject(sqlQueryForSource, String.class);
    }

    private static String getQueryResultTarget(String sqlQueryForTarget) throws Exception {
        return dbHelper.targetTemplate().queryForObject(sqlQueryForTarget, String.class);
    }

    private static void verifyOutcome(String resultSource, String resultTarget) {
        logger.info("Result from the source is: " + resultSource);
        logger.info("Result from the target is: " + resultTarget);

        assertThat("The outcome from the source and the target is not equal.",
                resultSource, is(resultTarget));
    }

}
