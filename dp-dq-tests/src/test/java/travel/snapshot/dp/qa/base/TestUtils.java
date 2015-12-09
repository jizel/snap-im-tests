package travel.snapshot.dp.qa.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.ConfigProps;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

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

        logger.info("Result from the source is: " + outcomeSource);
        logger.info("Result from the target is: " + outcomeTarget);

        assertThat("The outcome from the source and the target is not equal.",
        		outcomeTarget, is(outcomeSource));
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
    
    public static int getQueryResultInt(String sql) throws Exception {
        return dbHelper.targetTemplate().queryForObject(sql, int.class);
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
    
    public static void followUpLoadTestFacebook(List<String> factsYesterday, List<String> incrementalsToday, List<String> factsToday, List<String> metrics) throws Exception {
        
        Iterator<String> it1 = factsYesterday.iterator();
        Iterator<String> it2 = incrementalsToday.iterator();
        Iterator<String> it3 = factsToday.iterator();
        Iterator<String> it4 = metrics.iterator();
        
        while (it1.hasNext() && it2.hasNext() && it3.hasNext() && it4.hasNext()) {
          String followUpFactsYesterday = it1.next();
          String followUpIncrementalsToday = it2.next();
          String followUpFactsToday = it3.next();
          String metric = it4.next();
          
          logger.info("Metric - " + metric);
          logger.info("Facts yesterday: " + getQueryResultInt(followUpFactsYesterday));
          logger.info("Incrementals today: " + getQueryResultInt(followUpIncrementalsToday));
          logger.info("Facts today: " + getQueryResultInt(followUpFactsToday));
          
          assertEquals(
        		  getQueryResultInt(followUpFactsYesterday) +
        		  getQueryResultInt(followUpIncrementalsToday),
        		  getQueryResultInt(followUpFactsToday));
        }
      }
    
    public static void followUpLoadTestTwitter(List<Integer> source, List<Integer> target) throws Exception {
        
        Iterator<Integer> it1 = source.iterator();
        Iterator<Integer> it2 = target.iterator();
        
        while (it1.hasNext() && it2.hasNext()) {
            logger.info("Source: " + it1.next());
            logger.info("Target: " + it2.next());
            assertEquals(it1.next(), it2.next());
        }
      }
}
