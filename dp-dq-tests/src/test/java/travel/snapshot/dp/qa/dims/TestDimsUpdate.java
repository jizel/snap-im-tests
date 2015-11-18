package travel.snapshot.dp.qa.dims;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import travel.snapshot.dp.qa.EtlRunner;
import travel.snapshot.dp.qa.base.InsertingDataRule;

import static travel.snapshot.dp.qa.base.TestUtils.testLoad;
import static travel.snapshot.dp.qa.base.TestUtils.withStartDate;

/**
 * Updates source dimensional tables, runs ETL and then checks whether target dimension tables
 * have been updated.
 */
public class TestDimsUpdate {

    @ClassRule
    public static InsertingDataRule dataRule = new InsertingDataRule("dimension_update.sql");


    @BeforeClass
    public static void runEtl() {
        // no need to transfer fact table data (--dimonly); since --startdate is ommited the ETL
        // tool should use timestamp of last OK execution
        EtlRunner.runEtl(new String[]{ "--dimonly" });
    }


    @Test
    public void testDimChannelCategoryUpdate() throws Exception {
        testLoad(
                withStartDate("SELECT count(ID) FROM ChannelCategories WHERE ModificationTimestamp > %s"),
                // Need to subtract 1 because currently there are initial data added in the table
                "SELECT count(*)-1 FROM dim_channel_category");
    }

}
