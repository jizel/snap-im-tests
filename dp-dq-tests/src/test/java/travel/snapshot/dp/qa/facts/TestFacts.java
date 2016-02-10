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
    String sqlQueryForSource = withStartDate(
        "select sum(CNT) as CNTs from (select count(*) as CNT from FCSGridCurrentElements ce "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null)"
            + " or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or "
            + " (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))"
            + " left join ( select PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) "
            + " FinishedTimestamp from AggregationAffectedDateRanges where Status = 2 and AggregationType IN "
            + " (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID "
            + " and agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where "
            + " (ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null) union all select count(*) "
            + " as CNT from FCSGridSnapshotElements se join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s) z ");

    String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates";

    logger.info("Start control checks on table 'fact_otb_stay_dates'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select coalesce(sum(OTBTen_RmNtsSum),0) a, coalesce(cast(sum(OTBTen_RevenueSum) as decimal (18,2)),0) b, coalesce(sum(OTBTen_NumberOfReservationsSum),0) c, coalesce(sum(OTBDef_RmNtsSum),0) d, coalesce(cast(sum(OTBDef_RevenueSum) as decimal (18,2)),0) e, "
            + " coalesce(sum(OTBDef_NumberOfReservationsSum),0) f, coalesce(sum(AvailableRoomsSum),0) g , coalesce(sum(ActiveBudget_RmNtsSum),0) h, coalesce(sum(ActiveBudget_RevenueSum),0) i "
            + " from ( select coalesce(sum(OTBTen_RmNts),0) OTBTen_RmNtsSum, coalesce(cast(sum(OTBTen_Revenue) as decimal (18,2)),0) OTBTen_RevenueSum, coalesce(sum(OTBTen_NumberOfReservations),0) OTBTen_NumberOfReservationsSum, "
            + " coalesce(sum(OTBDef_RmNts),0) OTBDef_RmNtsSum, coalesce(cast(sum(OTBDef_Revenue) as decimal (18,2)),0) OTBDef_RevenueSum, "
            + " coalesce(sum(OTBDef_NumberOfReservations),0) OTBDef_NumberOfReservationsSum, coalesce(sum(AvailableRooms),0) AvailableRoomsSum, coalesce(sum(ActiveBudget_RmNts),0) ActiveBudget_RmNtsSum, coalesce(sum(ActiveBudget_Revenue),0) ActiveBudget_RevenueSum"
            + " from FCSGridCurrentElements ce left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  "
            + " and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil)"
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))"
            + " left join ( select PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from AggregationAffectedDateRanges "
            + " where Status = 2 and AggregationType IN (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd "
            + " where (ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null) "
            + " union all select coalesce(sum(OTBTen_RmNts),0) OTBTen_RmNtsSum, coalesce(cast(sum(OTBTen_Revenue) as decimal (18,2)),0) OTBTen_RevenueSum, coalesce(sum(OTBTen_NumberOfReservations),0) OTBTen_NumberOfReservationsSum,"
            + " coalesce(sum(OTBDef_RmNts),0) OTBDef_RmNtsSum, coalesce(cast(sum(OTBDef_Revenue) as decimal (18,2)),0) OTBDef_RevenueSum, coalesce(sum(OTBDef_NumberOfReservations),0) OTBDef_NumberOfReservationsSum, 0 AvailableRoomsSum, 0 ActiveBudget_RmNtsSum, 0 ActiveBudget_RevenueSum "
            + " from FCSGridSnapshotElements se join FCSGridSnapshotsMetadata sm on sm.ID = se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or sm.SnapshotLastModificationTimestamp >= %1$s ) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(
        "select coalesce(sum(ten_room_nights),0) a, coalesce(cast(sum(ten_room_revenue) as decimal "
            + "(18,2)),0) b, coalesce(sum(ten_number_of_reservations),0) c, coalesce(sum(def_room_nights),0) "
            + "d, coalesce(cast(sum(def_room_revenue) as decimal (18,2)),0) e, "
            + "coalesce(sum(def_number_of_reservations),0) f, coalesce(sum(available_rooms),0) g, coalesce(sum(budget_room_nights),0) h, coalesce(sum(budget_revenue),0) i from fact_otb_stay_dates");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  @Test
  public void testFactOtbStayDatesChannelLoad() throws Exception {

    String sqlQueryForSource = withStartDate(
        "select sum(CNT) as CNTs from (select count(*) as CNT from FCSGridCurrentElementsChannels cec "
            + " join FCSGridCurrentElements ce on ce.ID = cec.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) "
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))  "
            + " left join ( select "
            + " PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) "
            + " FinishedTimestamp from AggregationAffectedDateRanges where Status = 2 and AggregationType IN "
            + " (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID "
            + " and agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where "
            + " ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null union all select count(*) "
            + " as CNT from FCSGridSnapshotElementsChannels sec join FCSGridSnapshotElements se on se.ID = "
            + " sec.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s) z");

    String sqlQueryForTarget = "select count(*) as CNTs from fact_otb_stay_dates_channel";

    logger.info("Start control checks on table 'fact_otb_stay_dates_channel'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select sum(OTBTen_RmNtsSum) a, sum(OTBTen_RevenueSum) b, sum(OTBTen_NumberOfReservationsSum) "
            + " c, sum(OTBDef_RmNtsSum) d, sum(OTBDef_RevenueSum) e, sum(OTBDef_NumberOfReservationsSum) f, coalesce(sum(AvailableRoomsSum),0) g from "
            + " ( select coalesce(sum(cec.OTBTen_RmNts),0) as OTBTen_RmNtsSum, "
            + " coalesce(cast(sum(cec.OTBTen_Revenue) as decimal (18,2)),0) as OTBTen_RevenueSum, "
            + " coalesce(sum(cec.OTBTen_NumberOfReservations),0) as OTBTen_NumberOfReservationsSum, "
            + " coalesce(sum(cec.OTBDef_RmNts),0) as OTBDef_RmNtsSum, coalesce(cast(sum(cec.OTBDef_Revenue) as decimal (18,2)),0) as OTBDef_RevenueSum, "
            + " coalesce(sum(cec.OTBDef_NumberOfReservations),0) as OTBDef_NumberOfReservationsSum, coalesce(sum(AvailableRooms),0) AvailableRoomsSum"
            + " from FCSGridCurrentElementsChannels cec join FCSGridCurrentElements ce on ce.ID = cec.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) "
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))  "
            + " left join ( select PropertyID, "
            + " MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from "
            + " AggregationAffectedDateRanges where Status = 2 and AggregationType IN (0) and FinishedTimestamp "
            + " > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and agg.DateStart <= "
            + " ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where ce.ModificationTimestamp >= %1$s or "
            + " agg.FinishedTimestamp is not null union all select coalesce(sum(sec.OTBTen_RmNts),0) as "
            + " OTBTen_RmNtsSum, coalesce(cast(sum(sec.OTBTen_Revenue) as decimal (18,2)),0) as "
            + " OTBTen_RevenueSum, coalesce(sum(sec.OTBTen_NumberOfReservations),0) as "
            + " OTBTen_NumberOfReservationsSum, coalesce(sum(sec.OTBDef_RmNts),0) as OTBDef_RmNtsSum, "
            + " coalesce(cast(sum(sec.OTBDef_Revenue) as decimal (18,2)),0) as OTBDef_RevenueSum, "
            + " coalesce(sum(sec.OTBDef_NumberOfReservations),0) as OTBDef_NumberOfReservationsSum, 0 AvailableRoomsSum from "
            + " FCSGridSnapshotElementsChannels sec join FCSGridSnapshotElements se on se.ID = "
            + " sec.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s ) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(
        "select coalesce(sum(ten_room_nights),0) a, coalesce(cast(sum(ten_room_revenue) as decimal "
            + " (18,2)),0) b, coalesce(sum(ten_number_of_reservations),0) c, coalesce(sum(def_room_nights),0) "
            + " d, coalesce(cast(sum(def_room_revenue) as decimal (18,2)),0) e, "
            + " coalesce(sum(def_number_of_reservations),0) f, coalesce(sum(available_rooms),0) g"
            + " from fact_otb_stay_dates_channel");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  @Test
  public void testFactOtbStayDatesChannelSegmentLoad() throws Exception {

    String sqlQueryForSource =
        withStartDate("select sum(CNT) as CNTs from (select count(*) as CNT from "
            + " FCSGridCurrentElementsChannelSegments currcs join FCSGridCurrentElements ce on ce.ID = "
            + " currcs.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) "
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))  "
            + " left join ( select PropertyID, MIN(DateStart) DateStart, "
            + " Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from "
            + " AggregationAffectedDateRanges where Status = 2 and AggregationType IN (0) and FinishedTimestamp "
            + " > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and agg.DateStart <= "
            + " ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where ce.ModificationTimestamp >= %1$s or "
            + " agg.FinishedTimestamp is not null union all select count(*) as CNT from "
            + " FCSGridSnapshotElementsChannelSegments snapcs join FCSGridSnapshotElements se on se.ID = "
            + " snapcs.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s) z");
    String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_channel_segment";

    logger.info("Start control checks on table 'fact_otb_stay_dates_channel_segment'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select coalesce(sum(OTBTen_RmNtsSum),0) a, coalesce(cast(sum(OTBTen_RevenueSum) as decimal "
            + " (18,2)),0) b, coalesce(sum(OTBDef_RmNtsSum),0) c, coalesce(cast(sum(OTBDef_RevenueSum) as "
            + " decimal (18,2)),0) d, coalesce(sum(AvailableRoomsSum),0) e, coalesce(sum(ActiveBudget_RmNtsSum),0) f, coalesce(sum(ActiveBudget_RevenueSum),0) g"
            + " from ( select coalesce(sum(currcs.OTBTen_RmNts),0) OTBTen_RmNtsSum, "
            + " coalesce(cast(sum(currcs.OTBTen_Revenue) as decimal (18,2)),0) OTBTen_RevenueSum, "
            + " coalesce(sum(currcs.OTBDef_RmNts),0) OTBDef_RmNtsSum, coalesce(cast(sum(currcs.OTBDef_Revenue) "
            + " as decimal (18,2)),0) OTBDef_RevenueSum, coalesce(sum(AvailableRooms),0) AvailableRoomsSum, coalesce(sum(currcs.ActiveBudget_RmNts),0) ActiveBudget_RmNtsSum, coalesce(sum(currcs.ActiveBudget_Revenue),0) ActiveBudget_RevenueSum "
            + " from FCSGridCurrentElementsChannelSegments currcs join FCSGridCurrentElements ce on ce.ID = currcs.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) "
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))  "
            + " left join ( select "
            + " PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) "
            + " FinishedTimestamp from AggregationAffectedDateRanges where Status = 2 and AggregationType IN "
            + " (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID "
            + " and agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where "
            + " ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null union all select "
            + " coalesce(sum(snapcs.OTBTen_RmNts),0) OTBTen_RmNtsSum, coalesce(cast(sum(snapcs.OTBTen_Revenue) "
            + " as decimal (18,2)),0) OTBTen_RevenueSum, coalesce(sum(snapcs.OTBDef_RmNts),0) OTBDef_RmNtsSum, "
            + " coalesce(cast(sum(snapcs.OTBDef_Revenue) as decimal (18,2)),0) OTBDef_RevenueSum, 0 AvailableRoomsSum, 0 ActiveBudget_RmNtsSum, 0 ActiveBudget_RevenueSum from "
            + " FCSGridSnapshotElementsChannelSegments snapcs join FCSGridSnapshotElements se on se.ID = "
            + " snapcs.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(
        "select coalesce(sum(ten_room_nights),0) a, coalesce(cast(sum(ten_room_revenue) as decimal "
            + "(18,2)),0) b, coalesce(sum(def_room_nights),0) c, coalesce(cast(sum(def_room_revenue) as "
            + "decimal (18,2)),0) d, coalesce(sum(available_rooms),0) e, coalesce(sum(budget_room_nights),0) f, coalesce(sum(budget_revenue),0) g from fact_otb_stay_dates_channel_segment");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  @Test
  public void testFactOtbStayDatesMarketSegmentLoad() throws Exception {
    String sqlQueryForSource = withStartDate(
        "select sum(CNT) as CNTs from (select count(*) as CNT from FCSGridCurrentElementsMarketSegments "
            + " currms join FCSGridCurrentElements ce on ce.ID = currms.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) "
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))  "
            + " left join ( select PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) "
            + " FinishedTimestamp from AggregationAffectedDateRanges where Status = 2 and AggregationType IN "
            + " (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID "
            + " and agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where "
            + " ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null union all select count(*) "
            + " as CNT from FCSGridSnapshotElementsMarketSegments snapms join FCSGridSnapshotElements se on "
            + " se.ID = snapms.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s) z");

    String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_market_segment";

    logger.info("Start control checks on table 'fact_otb_stay_dates_market_segment'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select coalesce(sum(OTBTen_RmNtsSum),0) a, coalesce(cast(sum(OTBTen_RevenueSum) as decimal "
            + " (18,2)),0) b, coalesce(sum(OTBDef_RmNtsSum),0) c, coalesce(cast(sum(OTBDef_RevenueSum) as "
            + " decimal (18,2)),0) d, coalesce(sum(AvailableRoomsSum),0) e, coalesce(sum(ActiveBudget_RmNtsSum),0) f, coalesce(sum(ActiveBudget_RevenueSum),0) g"
            + " from ( select coalesce(sum(currms.OTBTen_RmNts),0) OTBTen_RmNtsSum, "
            + " coalesce(cast(sum(currms.OTBTen_Revenue) as decimal (18,2)),0) OTBTen_RevenueSum, "
            + " coalesce(sum(currms.OTBDef_RmNts),0) OTBDef_RmNtsSum, coalesce(cast(sum(currms.OTBDef_Revenue) as decimal (18,2)),0) OTBDef_RevenueSum, "
            + " coalesce(sum(AvailableRooms),0) AvailableRoomsSum, "
            + " coalesce(sum(currms.ActiveBudget_RmNts),0) ActiveBudget_RmNtsSum, coalesce(sum(currms.ActiveBudget_Revenue),0) ActiveBudget_RevenueSum "
            + " from FCSGridCurrentElementsMarketSegments currms join "
            + " FCSGridCurrentElements ce on ce.ID = currms.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on pdri.PropertyId = ce.PropertyId  and ((pdri.DateFrom is null and pdri.DateUntil is null) or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) "
            + " or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))  "
            + " left join ( select PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) "
            + " FinishedTimestamp from AggregationAffectedDateRanges where Status = 2 and AggregationType IN "
            + " (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID "
            + " and agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where "
            + " ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null union all select "
            + " coalesce(sum(snapms.OTBTen_RmNts),0) OTBTen_RmNtsSum, coalesce(cast(sum(snapms.OTBTen_Revenue) "
            + " as decimal (18,2)),0) OTBTen_RevenueSum, coalesce(sum(snapms.OTBDef_RmNts),0) OTBDef_RmNtsSum, "
            + " coalesce(cast(sum(snapms.OTBDef_Revenue) as decimal (18,2)),0) OTBDef_RevenueSum, 0 AvailableRoomsSum, 0 ActiveBudget_RmNtsSum, 0 ActiveBudget_RevenueSum from "
            + " FCSGridSnapshotElementsMarketSegments snapms join FCSGridSnapshotElements se on se.ID = "
            + " snapms.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + " se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + " sm.SnapshotLastModificationTimestamp >= %1$s) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(
        "select coalesce(sum(ten_room_nights),0) a, coalesce(cast(sum(ten_room_revenue) as decimal (18,2)),0) b, coalesce(sum(def_room_nights),0) c, coalesce(cast(sum(def_room_revenue) as "
            + " decimal (18,2)),0) d, coalesce(sum(available_rooms),0) e, coalesce(sum(budget_room_nights),0) f, coalesce(sum(budget_revenue),0) g from fact_otb_stay_dates_market_segment");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  @Test
  public void testFactRrsChannelLoad() throws Exception {
    String sqlQueryForSource =
        withStartDate("select sum(CNT) as CNTs from (select count(*) as CNT from "
            + "FCSGridCurrentElementsRateRankShoppingChannels currRrsc join FCSGridCurrentElements ce on ce.ID "
            + "= currRrsc.FCSGridCurrentElementID left join ( select PropertyID, MIN(DateStart) DateStart, "
            + "Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from "
            + "AggregationAffectedDateRanges where Status = 2 and AggregationType IN ('1','9') and "
            + "FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and "
            + "agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where ce.ModificationTimestamp "
            + ">= %1$s or agg.FinishedTimestamp is not null union all select count(*) as CNT from "
            + "FCSGridSnapshotElementsRateRankShoppingChannels snapRrsc join FCSGridSnapshotElements se on "
            + "se.ID = snapRrsc.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + "se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + "sm.SnapshotLastModificationTimestamp >= %1$s) z");
    String sqlQueryForTarget = "select count(*) from fact_rrs_channel";

    logger.info("Start control checks on table 'fact_rrs_channel'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select coalesce(sum(PropInReg),0) a, coalesce(sum(ChRank),0) b from ( select "
            + "coalesce(sum(ce.ChannelRank_NumberOfPropertiesInRegion),0) PropInReg, "
            + "coalesce(sum(currRrsc.ChannelRank_ChannelRank),0) ChRank from "
            + "FCSGridCurrentElementsRateRankShoppingChannels currRrsc join FCSGridCurrentElements ce on ce.ID "
            + "= currRrsc.FCSGridCurrentElementID left join ( select PropertyID, MIN(DateStart) DateStart, "
            + "Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from "
            + "AggregationAffectedDateRanges where Status = 2 and AggregationType IN ('1','9') and "
            + "FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and "
            + "agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where ce.ModificationTimestamp "
            + ">= %1$s or agg.FinishedTimestamp is not null union all select "
            + "coalesce(sum(se.ChannelRank_NumberOfPropertiesInRegion),0) PropInReg, "
            + "coalesce(sum(snapRrsc.ChannelRank_ChannelRank),0) ChRank from "
            + "FCSGridSnapshotElementsRateRankShoppingChannels snapRrsc join FCSGridSnapshotElements se on "
            + "se.ID = snapRrsc.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + "se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + "sm.SnapshotLastModificationTimestamp >= %1$s) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(
        "select coalesce(sum(number_of_properties_in_region),0) a, coalesce(sum(rank),0) b from "
            + "fact_rrs_channel");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  @Test
  public void testFactRrsCompetitorLoad() throws Exception {
    String sqlQueryForSource =
        withStartDate("select sum(CNT) as CNTs from (select count(*) as CNT from "
            + "FCSGridCurrentElementsRateRankShoppingCompetitors currRrsc join FCSGridCurrentElements ce on "
            + "ce.ID = currRrsc.FCSGridCurrentElementID left join ( select PropertyID, MIN(DateStart) "
            + "DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from "
            + "AggregationAffectedDateRanges where Status = 2 and AggregationType IN ('1','9') and "
            + "FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and "
            + "agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where ce.ModificationTimestamp "
            + ">= %1$s or agg.FinishedTimestamp is not null union all select count(*) as CNT from "
            + "FCSGridSnapshotElementsRateRankShoppingCompetitors snapRrsc join FCSGridSnapshotElements se on "
            + "se.ID = snapRrsc.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + "se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + "sm.SnapshotLastModificationTimestamp >= %1$s) z");
    String sqlQueryForTarget = "select count(*) from fact_rrs_competitor";

    logger.info("Start control checks on table 'fact_rrs_competitor'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select coalesce(cast(sum(CompAvg) as decimal (18,2)),0) a, coalesce(cast(sum(HotelAvg) as "
            + "decimal (18,2)),0) b from ( select "
            + "coalesce(cast(sum(currRrsc.CompetitorBAR_CompetitorAverageBAR) as decimal (18,2)),0) CompAvg, "
            + "coalesce(cast(sum(ce.CompetitorBAR_HotelAverageBAR) as decimal (18,2)),0) HotelAvg from "
            + "FCSGridCurrentElementsRateRankShoppingCompetitors currRrsc join FCSGridCurrentElements ce on "
            + "ce.ID = currRrsc.FCSGridCurrentElementID left join ( select PropertyID, MIN(DateStart) "
            + "DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from "
            + "AggregationAffectedDateRanges where Status = 2 and AggregationType IN ('1','9') and "
            + "FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and "
            + "agg.DateStart <= ce.ArrivalDay and ce.ArrivalDay <= agg.DateEnd where ce.ModificationTimestamp "
            + ">= %1$s or agg.FinishedTimestamp is not null union all select "
            + "coalesce(cast(sum(snapRrsc.CompetitorBAR_CompetitorAverageBAR) as decimal (18,2)),0) CompAvg, "
            + "coalesce(cast(sum(se.CompetitorBAR_HotelAverageBAR) as decimal (18,2)),0) HotelAvg from "
            + "FCSGridSnapshotElementsRateRankShoppingCompetitors snapRrsc join FCSGridSnapshotElements se on "
            + "se.ID = snapRrsc.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = "
            + "se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s or "
            + "sm.SnapshotLastModificationTimestamp >= %1$s) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget
        .add("select coalesce(cast(sum(competitor_average_BAR) as decimal (18,2)),0) a, "
            + "coalesce(cast(sum(hotel_average_BAR) as decimal (18,2)),0) b from fact_rrs_competitor");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }


  @Test
  public void testFactCbDailyLoad() throws Exception {
    String sqlQueryForSource = withStartDate(
        "select count(p.ID) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = "
            + "CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs "
            + "where DataSourceType = 40 and TimestampFinish >= %1$s) and b.BenchmarkLevel = 1");
    String sqlQueryForTarget = "select count(*) from fact_cb_daily";

    logger.info("Start control checks on table 'fact_cb_daily'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource
        .add(withStartDate("select coalesce(sum(b.PropSupTY),0) a, coalesce(sum(b.PropDemTY),0) b, "
            + "coalesce(sum(b.PropSupLY),0) c, coalesce(sum(b.PropDemLY),0) d, coalesce(sum(b.CompSupTY),0) e, "
            + "coalesce(sum(b.CompDemTY),0) f, coalesce(sum(b.CompSupLY),0) g, coalesce(sum(b.CompDemLY),0) h, "
            + "coalesce(cast(sum(b.PropRevTY) as decimal (18,2)),0) i, coalesce(cast(sum(b.PropRevLY) as "
            + "decimal (18,2)),0) j, coalesce(cast(sum(b.CompRevTY) as decimal (18,2)),0) k, "
            + "coalesce(cast(sum(b.CompRevLY) as decimal (18,2)),0) l, coalesce(sum(b.OccRankTY),0) m, "
            + "coalesce(sum(b.OccRankCompetitorsTY),0) n, coalesce(sum(b.ADRRankTY),0) o, "
            + "coalesce(sum(b.ADRRankCompetitorsTY),0) p, coalesce(sum(b.RevPARRankTY),0) q, "
            + "coalesce(sum(b.RevPARRankCompetitorsTY),0) r from ImportedCompetitorBenchmarks b join "
            + "Properties p on (HotelCensusID = CompetitorBenchmarkHotelID) where RawDataImportLogID in ( "
            + "select ID from RawDataImportLogs where DataSourceType = 40 and TimestampFinish >= %1$s) and "
            + "b.BenchmarkLevel = 1"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget
        .add("select coalesce(sum(prop_sup_TY),0) a, coalesce(sum(prop_dem_TY),0) b, "
            + "coalesce(sum(prop_sup_LY),0) c, coalesce(sum(prop_dem_LY),0) d, coalesce(sum(comp_sup_TY),0) e, "
            + "coalesce(sum(comp_dem_TY),0) f, coalesce(sum(comp_sup_LY),0) g, coalesce(sum(comp_dem_LY),0) h, "
            + "coalesce(cast(sum(prop_rev_TY) as decimal (18,2)),0) i, coalesce(cast(sum(prop_rev_LY) as "
            + "decimal (18,2)),0) j, coalesce(cast(sum(comp_rev_TY) as decimal (18,2)),0) k, "
            + "coalesce(cast(sum(comp_rev_LY) as decimal (18,2)),0) l, coalesce(sum(occ_rank),0) m, "
            + "coalesce(sum(occ_rank_competitors),0) n, coalesce(sum(adr_rank),0) o, "
            + "coalesce(sum(adr_rank_competitors),0) p, coalesce(sum(RevPAR_rank),0) q, "
            + "coalesce(sum(RevPAR_rank_competitors),0) r from fact_cb_daily");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  @Test
  public void testFactCbRanksLoad() throws Exception {
    String sqlQueryForSource = withStartDate(
        "select count(p.ID) from ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = "
            + "CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs "
            + "where DataSourceType = 40 and TimestampFinish >= %1$s) and b.BenchmarkLevel <> 1");
    String sqlQueryForTarget = "select count(*) from fact_cb_ranks";

    logger.info("Start control checks on table 'fact_cb_ranks'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select coalesce(sum(b.OccRankTY),0) a, coalesce(sum(b.OccRankCompetitorsTY),0) b, "
            + "coalesce(sum(b.ADRRankTY),0) c, coalesce(sum(b.ADRRankCompetitorsTY),0) d, "
            + "coalesce(sum(b.RevPARRankTY),0) e, coalesce(sum(b.RevPARRankCompetitorsTY),0) f from "
            + "ImportedCompetitorBenchmarks b join Properties p on (HotelCensusID = "
            + "CompetitorBenchmarkHotelID) where RawDataImportLogID in ( select ID from RawDataImportLogs "
            + "where DataSourceType = 40 and TimestampFinish >= %1$s) and b.BenchmarkLevel <> 1"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget
        .add("select coalesce(sum(occ_rank),0) a, coalesce(sum(occ_rank_competitors),0) b, "
            + "coalesce(sum(adr_rank),0) c, coalesce(sum(adr_rank_competitors),0) d, "
            + "coalesce(sum(RevPAR_rank),0) e, coalesce(sum(RevPAR_rank_competitors),0) f from "
            + "fact_cb_ranks");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }

  // The cube still is not included in the DWH currently there is only definition for the table
  //
  // @Test
  // public void testFactOotbSstayDatesRatePlan() throws Exception {
  // String sqlQueryForSource = withStartDate("");
  // String sqlQueryForTarget = "";
  //
  // logger.info("Start control checks on table 'fact_otb_stay_dates_rate_plan'");
  // testLoad(sqlQueryForSource, sqlQueryForTarget);
  //
  // List<String> followUpListToSource = new ArrayList<String>();
  // followUpListToSource.add(withStartDate(""));
  //
  // List<String> followUpListToTarget = new ArrayList<String>();
  // followUpListToTarget.add("");
  //
  // followUpLoadTest(followUpListToSource, followUpListToTarget);
  // }

  @Test
  public void testFactOtbStayDatesRoomType() throws Exception {
    String sqlQueryForSource =
        withStartDate("select sum(CNT) as CNTs from (select count(*) as CNT from"
            + " FCSGridCurrentElementsRoomTypes currcs join FCSGridCurrentElements ce"
            + " on ce.ID = currcs.FCSGridCurrentElementID "
            + " left join PropertyDynamicRoomsInventories pdri on ce.PropertyId = pdri.PropertyId and ((pdri.DateFrom is null and pdri.DateUntil is null)"
            + " or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom) "
            + " or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))"
            + " left join ( select PropertyID, MIN(DateStart) DateStart,"
            + " Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from"
            + " AggregationAffectedDateRanges where Status = 2 and AggregationType IN (0) and FinishedTimestamp > %1$s"
            + " group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and agg.DateStart <= ce.ArrivalDay"
            + " and ce.ArrivalDay <= agg.DateEnd where (ce.ModificationTimestamp >= %1$s"
            + " or agg.FinishedTimestamp is not null) union all "
            + " select count(*) as CNT from FCSGridSnapshotElementsRoomTypes snapcs"
            + " join FCSGridSnapshotElements se on se.ID = snapcs.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm "
            + " on sm.ID = se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s"
            + " or sm.SnapshotLastModificationTimestamp >= %1$s) z");

    String sqlQueryForTarget = "select count(*) from fact_otb_stay_dates_room_type";

    logger.info("Start control checks on table 'fact_otb_stay_dates_room_type'");
    testLoad(sqlQueryForSource, sqlQueryForTarget);

    List<String> followUpListToSource = new ArrayList<String>();
    followUpListToSource.add(withStartDate(
        "select  coalesce(sum(OTBTen_RmNtsSum),0) a, coalesce(sum(OTBTen_RevenueSum),0) b, coalesce(sum(OTBDef_RmNtsSum),0) c,"
            + " coalesce(sum(OTBDef_RevenueSum),0) d, coalesce(sum(AvailableRoomsSum),0) e from (select coalesce(sum(currcs.OTBTen_RmNts),0) OTBTen_RmNtsSum,"
            + " coalesce(sum(currcs.OTBTen_Revenue),0) OTBTen_RevenueSum, coalesce(sum(currcs.OTBDef_RmNts),0) OTBDef_RmNtsSum, coalesce(sum(currcs.OTBDef_Revenue),0) OTBDef_RevenueSum,"
            + " coalesce(sum(pdri.AvailableRooms),0) AvailableRoomsSum from FCSGridCurrentElementsRoomTypes currcs join FCSGridCurrentElements ce on ce.ID = currcs.FCSGridCurrentElementID"
            + " left join PropertyDynamicRoomsInventories pdri on ce.PropertyId = pdri.PropertyId and ((pdri.DateFrom is null and pdri.DateUntil is null)"
            + " or (pdri.DateFrom is null and ce.ArrivalDay <= pdri.DateUntil) or (pdri.DateUntil is null and ce.ArrivalDay >= pdri.DateFrom)"
            + " or (ce.ArrivalDay >= pdri.DateFrom and ce.ArrivalDay <= pdri.DateUntil))"
            + " left join ( select PropertyID, MIN(DateStart) DateStart, Max(DateEnd) DateEnd, MAX(FinishedTimestamp) FinishedTimestamp from AggregationAffectedDateRanges where Status = 2"
            + " and AggregationType IN (0) and FinishedTimestamp > %1$s group by PropertyID ) agg on agg.PropertyID = ce.PropertyID and agg.DateStart <= ce.ArrivalDay and "
            + " ce.ArrivalDay <= agg.DateEnd where (ce.ModificationTimestamp >= %1$s or agg.FinishedTimestamp is not null) union all select "
            + " coalesce(sum(snapcs.OTBTen_RmNts),0) OTBTen_RmNtsSum, coalesce(sum(snapcs.OTBTen_Revenue),0) OTBTen_RevenueSum, coalesce(sum(snapcs.OTBDef_RmNts),0) OTBDef_RmNtsSum,"
            + " coalesce(sum(snapcs.OTBDef_Revenue),0) OTBDef_RevenueSum, 0 AvailableRoomsSum from FCSGridSnapshotElementsRoomTypes snapcs join FCSGridSnapshotElements se"
            + " on se.ID = snapcs.FCSGridSnapshotElementID join FCSGridSnapshotsMetadata sm on sm.ID = se.FCSGridSnapshotsMetaDataID where sm.SnapshotCreationTimestamp >= %1$s"
            + " or sm.SnapshotLastModificationTimestamp >= %1$s) z"));

    List<String> followUpListToTarget = new ArrayList<String>();
    followUpListToTarget.add(
        "select coalesce(sum(ten_room_nights),0) a, coalesce(sum(ten_room_revenue),0) b, coalesce(sum(def_room_nights),0) c, "
            + "coalesce(sum(def_room_revenue),0) d, coalesce(sum(available_rooms),0) e from fact_otb_stay_dates_room_type");

    followUpLoadTest(followUpListToSource, followUpListToTarget);
  }
}
