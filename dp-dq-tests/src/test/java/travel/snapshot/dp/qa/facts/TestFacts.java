package travel.snapshot.dp.qa.facts;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

public class TestFacts {

    public static final Logger logger = LoggerFactory.getLogger(TestFacts.class);

    @Test
    public void testFactOtbStayDatesLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from FCSGridCurrentElements where ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates";

        logger.info("Start control checks on table 'fact_otb_stay_dates'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select coalesce(sum(OTBTen_RmNts),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(OTBTen_Revenue) as decimal (18,2)),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(OTBTen_NumberOfReservations),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(OTBDef_RmNts),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(OTBDef_Revenue) as decimal (18,2)),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(OTBDef_NumberOfReservations),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(sum(ten_number_of_reservations),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(sum(def_number_of_reservations),0) from fact_otb_stay_dates");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactOtbStayDatesChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel";

        logger.info("Start control checks on table 'fact_otb_stay_dates_channel'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_RmNts),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (18,2)),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_NumberOfReservations),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (18,2)),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_NumberOfReservations),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(sum(ten_number_of_reservations),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(sum(def_number_of_reservations),0) from fact_otb_stay_dates_channel");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);  
    }

    @Test
    public void testFactOtbStayDatesChannelSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel_segment";

        logger.info("Start control checks on table 'fact_otb_stay_dates_channel_segment'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
    
    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_RmNts),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
    followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (18,2)),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
    followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
    followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (18,2)),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates_channel_segment");
    followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates_channel_segment");
    followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates_channel_segment");
    followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates_channel_segment");
    
    followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactOtbStayDatesMarketSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_market_segment";

        logger.info("Start control checks on table 'fact_otb_stay_dates_market_segment'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
      List<String> followUpListToSource = new ArrayList<String>();
      followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_RmNts),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (18,2)),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (18,2)),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));

      List<String> followUpListToTarget = new ArrayList<String>();
      followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates_market_segment");
      followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates_market_segment");
      followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates_market_segment");
      followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (18,2)),0) from fact_otb_stay_dates_market_segment");

      followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
    
  //Implementation not finished - could be ignored for now   
    @Test
    public void testFactRrsChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsRateRankShoppingChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_rrs_channel";

        logger.info("Start control checks on table 'fact_rrs_channel'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
 
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select coalesce(sum(b.ChannelRank_NumberOfPropertiesInRegion),0) from FCSGridCurrentElementsRateRankShoppingChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.ChannelRank_ChannelRank),0) from FCSGridCurrentElementsRateRankShoppingChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
        
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(number_of_properties_in_region),0) from fact_rrs_channel");
        followUpListToTarget.add("select coalesce(sum(rank),0) from fact_rrs_channel");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

//Implementation not finished - could be ignored for now.    
    @Test
    public void testFactRrsCompetitorLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select Count(*) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_rrs_competitor";

        logger.info("Start control checks on table 'fact_rrs_competitor'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
      List<String> followUpListToSource = new ArrayList<String>();
      followUpListToSource.add(withStartDate("select coalesce(sum(b.CompetitorBAR_NumberOfCompetitors),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.CompetitorBAR_CompetitorAverageBAR) as decimal (18,2)),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(sum(a.CompetitorBAR_CompetitorRank),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(b.CompetitorBAR_HotelAverageBAR) as decimal (18,2)),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));


      List<String> followUpListToTarget = new ArrayList<String>();
      followUpListToTarget.add("select coalesce(sum(number_of_competitors),0) from fact_rrs_competitor");
      followUpListToTarget.add("select coalesce(cast(sum(competitor_average_BAR) as decimal (18,2)),0) from fact_rrs_competitor");
      followUpListToTarget.add("select coalesce(sum(competitor_rank),0) from fact_rrs_competitor");
      followUpListToTarget.add("select coalesce(cast(sum(hotel_average_BAR) as decimal (18,2)),0) from fact_rrs_competitor");
      
      followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    
    @Test
    public void testFactCbDailyLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(p.ID) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1");
        String sqlQueryForTarget = "select count(*) from fact_cb_daily";

        logger.info("Start control checks on table 'fact_cb_daily'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
      List<String> followUpListToSource = new ArrayList<String>();
      followUpListToSource.add(withStartDate("select coalesce(sum(b.PropSupTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.PropDemTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.PropSupLY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.PropDemLY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.CompSupTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.CompDemTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.CompSupLY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.CompDemLY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(b.PropRevTY) as decimal (18,2)),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(b.PropRevLY) as decimal (18,2)),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(b.CompRevTY) as decimal (18,2)),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(b.CompRevLY) as decimal (18,2)),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));

      followUpListToSource.add(withStartDate("select coalesce(sum(b.OccRankTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.OccRankCompetitorsTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.ADRRankTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.ADRRankCompetitorsTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.RevPARRankTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.RevPARRankCompetitorsTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel = 1"));
      
      List<String> followUpListToTarget = new ArrayList<String>();
      followUpListToTarget.add("select coalesce(sum(prop_sup_TY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(prop_dem_TY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(prop_sup_LY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(prop_dem_LY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(comp_sup_TY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(comp_dem_TY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(comp_sup_LY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(comp_dem_LY),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(cast(sum(prop_rev_TY) as decimal (18,2)),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(cast(sum(prop_rev_LY) as decimal (18,2)),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(cast(sum(comp_rev_TY) as decimal (18,2)),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(cast(sum(comp_rev_LY) as decimal (18,2)),0) from fact_cb_daily");
      
      followUpListToTarget.add("select coalesce(sum(occ_rank),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(occ_rank_competitors),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(adr_rank),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(adr_rank_competitors),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(RevPAR_rank),0) from fact_cb_daily");
      followUpListToTarget.add("select coalesce(sum(RevPAR_rank_competitors),0) from fact_cb_daily");
      
      followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
    
    @Test
    public void testFactCbRanksLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(p.ID) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1");
        String sqlQueryForTarget = "select count(*) from fact_cb_ranks";

        logger.info("Start control checks on table 'fact_cb_ranks'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
      List<String> followUpListToSource = new ArrayList<String>();
      followUpListToSource.add(withStartDate("select coalesce(sum(b.OccRankTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.OccRankCompetitorsTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.ADRRankTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.ADRRankCompetitorsTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.RevPARRankTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1"));
      followUpListToSource.add(withStartDate("select coalesce(sum(b.RevPARRankCompetitorsTY),0) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %s) and b.BenchmarkLevel <> 1"));


      List<String> followUpListToTarget = new ArrayList<String>();
      followUpListToTarget.add("select coalesce(sum(occ_rank),0) from fact_cb_ranks");
      followUpListToTarget.add("select coalesce(sum(occ_rank_competitors),0) from fact_cb_ranks");
      followUpListToTarget.add("select coalesce(sum(adr_rank),0) from fact_cb_ranks");
      followUpListToTarget.add("select coalesce(sum(adr_rank_competitors),0) from fact_cb_ranks");
      followUpListToTarget.add("select coalesce(sum(RevPAR_rank),0) from fact_cb_ranks");
      followUpListToTarget.add("select coalesce(sum(RevPAR_rank_competitors),0) from fact_cb_ranks");

      followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
}
