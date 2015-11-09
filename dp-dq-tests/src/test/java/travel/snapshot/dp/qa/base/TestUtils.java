package travel.snapshot.dp.qa.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.ConfigProps;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.List;

public class TestUtils {

    public static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    public static final DbHelper dbHelper = new DbHelper();

    private TestUtils() {
        throw new AssertionError("Utility class - DO NOT INSTANTIATE!");
    }

    public static void testLoad(String sqlQueryForSource, String sqlQueryForTarget) throws Exception {
        final String outcomeSource = getQueryResultSource(sqlQueryForSource);
        final String outcomeTarget = getQueryResultTarget(sqlQueryForTarget);

        verifyOutcome(outcomeSource, outcomeTarget);
    }

    public static String withStartDate(String queryWithPlaceholder) {
        final String startDate = ConfigProps.getPropValue("etl.startdate");
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
                resultTarget, is(resultSource));
    }

    public static void followUpLoadTest(List<String> followUpListToSource, List<String> followUpListToTarget) throws Exception {
      
      Iterator<String> it1 = followUpListToSource.iterator();
      Iterator<String> it2 = followUpListToTarget.iterator();
      
      while (it1.hasNext() && it2.hasNext()) {
        String followUpQuerySource = it1.next();
        String followUpQueryTarget = it2.next();
        testLoad(followUpQuerySource, followUpQueryTarget);
      }
    }
}
