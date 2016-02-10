package travel.snapshot.dp.qa.web_performance;

import static travel.snapshot.dp.qa.base.TestUtils.followUpLoadTest;
import static travel.snapshot.dp.qa.base.TestUtils.testLoad;
import static travel.snapshot.dp.qa.base.TestUtils.withStartDate;
import static travel.snapshot.dp.qa.base.TestUtils.getQueryResultSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.base.DbHelper;
import travel.snapshot.dp.qa.base.TestUtils;


public class TestWebPerformanceLoad {
    
  public static final Logger logger = LoggerFactory.getLogger(TestUtils.class);
  public static final DbHelper dbHelper = new DbHelper();
  
  
  @Test
  public void testFactWebPerformanceLoad() throws Exception {
  // Could be compared with collection for noon of midnight
    String sqlQueryForSource = "select count(rt.uid) from Raw_imported_web_performance rt inner join Dim_country ct on rt.country_iso_code = ct.country_iso_code"
        + " inner join Dim_wp_source st on rt.ref_source = st.source_name inner join Dim_wp_default_channel dct on rt.default_channel_grouping = dct.channel_name"
        + " where rt.data_collection_run = '1' and rt.date_id in (select date_id from Dim_date where day <= (curdate()-1))";
    
    String sqlQueryForTarget = "select count(dim_property_id) from Fact_web_performance wp"
        + " where wp.dim_date_id in (select date_id from Dim_date where day <= (curdate()-1))";
    
    logger.info("Start control checks on table 'Fact_web_performance'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add("select coalesce(sum(total_unique_users),0) a, coalesce(sum(total_sessions),0) b,"
        + " coalesce(sum(total_transactions),0) c, coalesce(cast(sum(total_revenue) as decimal (18,2)),0) d from Raw_imported_web_performance rt "
        + " inner join Dim_country ct on rt.country_iso_code = ct.country_iso_code"
        + " inner join Dim_wp_source st on rt.ref_source = st.source_name "
        + " inner join Dim_wp_default_channel dct on rt.default_channel_grouping = dct.channel_name"
        + " where rt.data_collection_run = '1' and rt.date_id in (select date_id from Dim_date where day <= (curdate()-1))");

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add("select coalesce(sum(wp.users),0) a, coalesce(sum(wp.visits),0) b, "
        + " coalesce(sum(wp.transactions),0) c, coalesce(cast(sum(wp.revenue) as decimal (18,2)),0) d from Fact_web_performance wp"
        + " where wp.dim_date_id in (select date_id from Dim_date where day <= (curdate()-1))");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }
  
  @Test
  public void testFactWebPerformanceComulativeLoad() throws Exception {
  // Current staging has only data collection for noon (1) and for continuous execution the verifications should be for collection at midnight (2) for the previous day 
    String sqlQueryForSource = "select count(*) from ( select count(*), rt.date_id from Raw_imported_web_performance rt"
        + " inner join Dim_wp_source st on rt.ref_source = st.source_name inner join Dim_wp_default_channel dct "
        + " on rt.default_channel_grouping = dct.channel_name inner join Dim_country ct on rt.country_iso_code = ct.country_iso_code"
        + " where rt.data_collection_run = '2' and rt.date_id <= (curdate()-1) group by rt.date_id ) z";

    String sqlQueryForTarget = "select count(*) from Fact_web_performance_cumulative wpc where wpc.dim_date_id <= (curdate()-1)";

    logger.info("Start control checks on table 'Fact_web_performance_commulative'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(" select coalesce(sum(rt.total_unique_users),0) users, coalesce(sum(rt.total_sessions),0) visits,"
        + " coalesce(sum(rt.total_transactions),0) transactions, coalesce(cast(sum(rt.total_revenue) as decimal (18,2)),0) revenue"
        + " from Raw_imported_web_performance rt where rt.property_id = 352"
        + " and rt.date_id <= (curdate()-1)  and rt.data_collection_run = 2 ");

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(" select users, visits, transactions, cast(revenue as decimal (18,2)) revenue from Fact_web_performance_cumulative wp where wp.dim_date_id = (curdate()-1)");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }
}
