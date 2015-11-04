package travel.snapshot.dp.qa.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
