package travel.snapshot.dp.qa.facts;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

public class TestFacts {

    public static final Logger logger = LoggerFactory.getLogger(TestFacts.class);

    @Test
    public void testFactOtbStayDatesLoad() throws Exception {
        String sqlQueryForSource = withStartDate("SELECT count(ID) FROM FCSGridCurrentElements where ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates'");
        testFactLoad(sqlQueryForSource, sqlQueryForTarget);
    }

    @Test
    public void testFactOtbStayDatesChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("SELECT count(*) FROM FCSGridCurrentElementsChannels a JOIN FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates_channel'");
        testFactLoad(sqlQueryForSource, sqlQueryForTarget);
    }

    @Test
    public void testFactOtbStayDatesChannelLoadSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("SELECT count(*) FROM FCSGridCurrentElementsChannelSegments a JOIN FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel_segment";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates_channel_segment'");
        testFactLoad(sqlQueryForSource, sqlQueryForTarget);
    }

    @Test
    public void testFactOtbStayDatesMarketSegmentLoad() throws Exception {
        String sqlQueryForSource = withStartDate("SELECT count(*) FROM FCSGridCurrentElementsMarketSegments a JOIN FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_market_segment";

        logger.info("\nStart control checks on table 'fact_otb_stay_dates_market_segment'");
        testFactLoad(sqlQueryForSource, sqlQueryForTarget);
    }

    @Test
    public void testFactRrsChannelLoad() throws Exception {
        String sqlQueryForSource = withStartDate("SELECT count(*) FROM FCSGridCurrentElementsRateRankShoppingChannels a JOIN FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_rrs_channel";

        logger.info("\nStart control checks on table 'fact_rrs_channel'");
        testFactLoad(sqlQueryForSource, sqlQueryForTarget);
    }

    @Test
    public void testFactRrsCompetitorLoad() throws Exception {
        String sqlQueryForSource = withStartDate("SELECT Count(*) FROM FCSGridCurrentElementsRateRankShoppingCompetitors a JOIN FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > %s");
        String sqlQueryForTarget = "select count(*) from fact_rrs_competitor";

        logger.info("\nStart control checks on table 'fact_rrs_competitor'");
        testFactLoad(sqlQueryForSource, sqlQueryForTarget);
    }

}
