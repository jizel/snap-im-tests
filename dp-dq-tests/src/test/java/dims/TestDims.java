package dims;

import org.junit.Test;

import base.TestBase;

public class TestDims extends TestBase {

	@Test
	public void testDimChannelLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.Channels WHERE ModificationTimestamp > '2015-09-01'";
		// Need to subtract 1 because currently there are initial data added in
		// the table
		String sqlQueryForTarget = "SELECT count(*)-1 FROM dim_channel";

		System.out.println("\nStart control checks on table 'dim_channel'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimChannelCategoryLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.ChannelCategories WHERE ModificationTimestamp > '2015-09-01'";
		// Need to subtract 1 because currently there are initial data added in
		// the table
		String sqlQueryForTarget = "SELECT count(*)-1 FROM dim_channel_category";

		System.out.println("\nStart control checks on table 'dim_channel_category'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimChannelSegmentLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.ChannelSegments WHERE ModificationTimestamp > '2015-09-01'";
		// Need to subtract 1 because currently there are initial data added in
		// the table
		String sqlQueryForTarget = "SELECT count(*)-1 FROM dim_channel_segment";

		System.out.println("\nStart control checks on table 'dim_channel_segment'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimMarketCategoryLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.MarketCategories WHERE ModificationTimestamp > '2015-09-01'";
		// Need to subtract 1 because currently there are initial data added in
		// the table
		String sqlQueryForTarget = "SELECT count(*)-1 FROM dim_market_category";

		System.out.println("\nStart control checks on table 'dim_market_category'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimMarketSegmentLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.MarketSegments WHERE ModificationTimestamp > '2015-09-01'";
		// Need to subtract 1 because currently there are initial data added in
		// the table
		String sqlQueryForTarget = "SELECT count(*)-1 FROM dim_market_segment";

		System.out.println("\nStart control checks on table 'dim_market_segment'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimPropertyLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.Properties WHERE ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "SELECT count(*) FROM dim_property";

		System.out.println("\nStart control checks on table 'dim_property'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimRatePlanLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.RatePlans WHERE ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "SELECT count(*) FROM dim_rate_plan";

		System.out.println("\nStart control checks on table 'dim_rate_plan'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimRoomTypeLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.RoomTypes WHERE ModificationTimestamp > '2015-09-01'";
		String sqlQueryForTarget = "SELECT count(*) FROM dim_room_type";

		System.out.println("\nStart control checks on table 'dim_room_type'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}

	@Test
	public void testDimSnapshotLoad() throws Exception {
		String sqlQueryForSource = "SELECT count(ID) FROM SnapshotForDevelopers2.dbo.FCSGridSnapshotsMetadata WHERE SnapshotCreationTimestamp > '2015-09-01'";
		// Need to subtract 1 because currently there are initial data added in
		// the table
		String sqlQueryForTarget = "SELECT count(*)-1 FROM dim_snapshot";

		System.out.println("\nStart control checks on table 'dim_snapshot'");
		testFactLoad(sqlQueryForSource, sqlQueryForTarget);
	}
}
