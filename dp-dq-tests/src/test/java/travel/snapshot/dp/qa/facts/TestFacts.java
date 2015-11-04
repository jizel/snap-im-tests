package travel.snapshot.dp.qa.facts;

import org.junit.Test;

import travel.snapshot.dp.qa.base.TestBase;

public class TestFacts extends TestBase {

	@Test
	public void testFactOtbStayDatesLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElements where ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates";

		System.out.println("\nStart control checks on table 'fact_otb_stay_dates'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testFactOtbStayDatesChannelLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(*) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElementsChannels a JOIN SnapshotForDevelopers2.dbo.FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) where ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel";

		System.out.println("\nStart control checks on table 'fact_otb_stay_dates_channel'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testFactOtbStayDatesChannelLoadSegmentLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(*) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElementsChannelSegments a JOIN SnapshotForDevelopers2.dbo.FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel_segment";

		System.out.println("\nStart control checks on table 'fact_otb_stay_dates_channel_segment'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testFactOtbStayDatesMarketSegmentLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(*) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElementsMarketSegments a JOIN SnapshotForDevelopers2.dbo.FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_market_segment";

		System.out.println("\nStart control checks on table 'fact_otb_stay_dates_market_segment'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testFactRrsChannelLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(*) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElementsRateRankShoppingChannels a JOIN SnapshotForDevelopers2.dbo.FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "select count(*) from fact_rrs_channel";

		System.out.println("\nStart control checks on table 'fact_rrs_channel'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testFactRrsCompetitorLoad() throws Exception {
		String sqlQueryForSource = "SELECT Count(*) FROM SnapshotForDevelopers2.dbo.FCSGridCurrentElementsRateRankShoppingCompetitors a JOIN SnapshotForDevelopers2.dbo.FCSGridCurrentElements b ON (b.ID = a.FCSGridCurrentElementID) WHERE b.ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "select count(*) from fact_rrs_competitor";

		System.out.println("\nStart control checks on table 'fact_rrs_competitor'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

}
