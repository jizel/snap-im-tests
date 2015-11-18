package travel.snapshot.dp.qa.dims;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

public class TestDims {
    
    public static final Logger logger = LoggerFactory.getLogger(TestDims.class);

    @Test
    public void testDimChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from Channels where ModificationTimestamp > %s");

        // Need to subtract 1 because currently there are initial data added in
        // the table
        String sqlQueryForTarget = "select count(*)-1 from dim_channel";

        logger.info("Start control checks on table 'dim_channel'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct Name) from Channels where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name)-1 from dim_channel");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimChannelCategoryLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from ChannelCategories where ModificationTimestamp > %s");
        // Need to subtract 1 because currently there are initial data added in
        // the table
        String sqlQueryForTarget = "select count(*)-1 from dim_channel_category";

        logger.info("Start control checks on table 'dim_channel_category'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct Description) from ChannelCategories where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct description)-1 from dim_channel_category");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimChannelSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from ChannelSegments where ModificationTimestamp > %s");
        // Need to subtract 1 because currently there are initial data added in
        // the table
        String sqlQueryForTarget = "select count(*)-1 from dim_channel_segment";

        logger.info("Start control checks on table 'dim_channel_segment'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct ChannelSegmentName) from ChannelSegments where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name)-1 from dim_channel_segment");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimMarketCategoryLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from MarketCategories where ModificationTimestamp > %s");
        // Need to subtract 1 because currently there are initial data added in
        // the table
        String sqlQueryForTarget = "select count(*)-1 from dim_market_category";

        logger.info("Start control checks on table 'dim_market_category'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct MarketCategoryName) from MarketCategories where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name)-1 from dim_market_category");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimMarketSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from MarketSegments where ModificationTimestamp > %s");
        // Need to subtract 1 because currently there are initial data added in
        // the table
        String sqlQueryForTarget = "select count(*)-1 from dim_market_segment";

        logger.info("Start control checks on table 'dim_market_segment'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct MarketSegmentName) from MarketSegments where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name)-1 from dim_market_segment");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimPropertyLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from Properties where ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from dim_property";

        logger.info("Start control checks on table 'dim_property'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct PropertyName) from Properties where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name) from dim_property");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimRatePlanLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from RatePlans where ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from dim_rate_plan";

        logger.info("Start control checks on table 'dim_rate_plan'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct RatePlanCode) from RatePlans where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct code) from dim_rate_plan");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimRoomTypeLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from RoomTypes where ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from dim_room_type";

        logger.info("Start control checks on table 'dim_room_type'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct Name) from RoomTypes where ModificationTimestamp > %s"));
        followUpListToSource.add(withStartDate("select sum(NumberOfRooms) from RoomTypes where ModificationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name) from dim_room_type");
        followUpListToTarget.add("select sum(number_of_rooms) from dim_room_type");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testDimRateShoppingChannelLoad() throws Exception {
        String sqlQueryForSource = "select count(*) from RateRankShoppingChannels";
        String sqlQueryForTarget = "select count(*) from dim_rate_shopping_channel";

        logger.info("Start control checks on table 'dim_rate_shopping_channel'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select count(distinct ChannelName) from RateRankShoppingChannels");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name) from dim_rate_shopping_channel");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
    
    @Test
    public void testDimRateShoppingPropertyLoad() throws Exception {
        String sqlQueryForSource = "select count(distinct rrs.id) from RateRankShoppingProperties rrs left join Properties p on  rrs.ID = p.RateRankShoppingPropertyID";
        String sqlQueryForTarget = "select count(*) from dim_rate_shopping_property";

        logger.info("Start control checks on table 'dim_rate_shopping_property'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select count(distinct rrs.PropertyName) from RateRankShoppingProperties rrs left join Properties p on rrs.ID = p.RateRankShoppingPropertyID");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name) from dim_rate_shopping_property");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
    
    @Test
    public void testDimSnapshotLoad() throws Exception {
        String sqlQueryForSource = withStartDate("select count(ID) from FCSGridSnapshotsMetadata where SnapshotCreationTimestamp > %s");
        // Need to subtract 1 because currently there are initial data added in
        // the table
        String sqlQueryForTarget = "select count(*)-1 from dim_snapshot";

        logger.info("Start control checks on table 'dim_snapshot'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add(withStartDate("select count(distinct Caption) from FCSGridSnapshotsMetadata where SnapshotCreationTimestamp > %s"));
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select count(distinct name)-1 from dim_snapshot");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }
}
