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

        logger.info("\nStart control checks on table 'fact_otb_stay_dates'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select coalesce(sum(OTBTen_RmNts),0) from FCSGridCurrentElements where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_NumberOfReservations),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_NumberOfReservations),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(sum(ten_number_of_reservations),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates");
        followUpListToTarget.add("select coalesce(sum(def_number_of_reservations),0) from fact_otb_stay_dates");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactOtbStayDatesChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates_channel'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_RmNts),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_NumberOfReservations),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_NumberOfReservations),0) from FCSGridCurrentElementsChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp >= %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(sum(ten_number_of_reservations),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates_channel");
        followUpListToTarget.add("select coalesce(sum(def_number_of_reservations),0) from fact_otb_stay_dates_channel");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
        
    }

    @Test
    public void testFactOtbStayDatesChannelSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel_segment";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates_channel_segment'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
    
    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_RmNts),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
    followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
    followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
    followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsChannelSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates_channel_segment");
    followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates_channel_segment");
    followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates_channel_segment");
    followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates_channel_segment");
    
    followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactOtbStayDatesMarketSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_market_segment";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates_market_segment'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
      List<String> followUpListToSource = new ArrayList<String>();
      followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBTen_RmNts),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBTen_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(sum(a.OTBDef_RmNts),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.OTBDef_Revenue) as decimal (10,2)),0) from FCSGridCurrentElementsMarketSegments a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));

      List<String> followUpListToTarget = new ArrayList<String>();
      followUpListToTarget.add("select coalesce(sum(ten_room_nights),0) from fact_otb_stay_dates_market_segment");
      followUpListToTarget.add("select coalesce(cast(sum(ten_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates_market_segment");
      followUpListToTarget.add("select coalesce(sum(def_room_nights),0) from fact_otb_stay_dates_market_segment");
      followUpListToTarget.add("select coalesce(cast(sum(def_room_revenue) as decimal (10,2)),0) from fact_otb_stay_dates_market_segment");

      followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
    
  //Implementation not finished - could be ignored for now   
    @Test
    public void testFactRrsChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(*) from FCSGridCurrentElementsRateRankShoppingChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_rrs_channel";

        logger.info("\nStart control checks on table 'fact_rrs_channel'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
 
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select coalesce(sum(b.ChannelRank_NumberOfPropertiesInRegion),0) from FCSGridCurrentElementsRateRankShoppingChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
        followUpListToSource.add(withStartDate("select coalesce(sum(a.ChannelRank_ChannelRank),0) from FCSGridCurrentElementsRateRankShoppingChannels a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
        
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(number_of_properties_in_region),0) from fact_rrs_channel");
        followUpListToTarget.add("select coalesce(sum(rank),0) from fact_rrs_channel");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

//Implementation not finished - could be ignored for now   
    @Test
    public void testFactRrsCompetitorLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select Count(*) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s");
        String sqlQueryForTarget = "select count(*) from fact_rrs_competitor";

        logger.info("\nStart control checks on table 'fact_rrs_competitor'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
      List<String> followUpListToSource = new ArrayList<String>();
      followUpListToSource.add(withStartDate("select coalesce(sum(b.CompetitorBAR_NumberOfCompetitors),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(a.CompetitorBAR_CompetitorAverageBAR) as decimal (10,2)),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(sum(a.CompetitorBAR_CompetitorRank),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));
      followUpListToSource.add(withStartDate("select coalesce(cast(sum(b.CompetitorBAR_HotelAverageBAR) as decimal (10,2)),0) from FCSGridCurrentElementsRateRankShoppingCompetitors a join FCSGridCurrentElements b on (b.ID = a.FCSGridCurrentElementID) where b.ModificationTimestamp >= %s"));


      List<String> followUpListToTarget = new ArrayList<String>();
      followUpListToTarget.add("select coalesce(sum(number_of_competitors),0) from fact_rrs_competitor");
      followUpListToTarget.add("select coalesce(cast(sum(competitor_average_BAR) as decimal (10,2)),0) from fact_rrs_competitor");
      followUpListToTarget.add("select coalesce(sum(competitor_rank),0) from fact_rrs_competitor");
      followUpListToTarget.add("select coalesce(cast(sum(hotel_average_BAR) as decimal (10,2)),0) from fact_rrs_competitor");
      
      followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

}
