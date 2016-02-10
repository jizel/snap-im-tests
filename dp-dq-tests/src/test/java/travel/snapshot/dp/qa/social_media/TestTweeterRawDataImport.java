package travel.snapshot.dp.qa.social_media;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.base.DbHelper;
import travel.snapshot.dp.qa.base.TestUtils;

public class TestTweeterRawDataImport {

  public static final Logger logger = LoggerFactory.getLogger(TestUtils.class);
  public static final DbHelper dbHelper = new DbHelper();

  @Test
  public void testTwitterRawDataImport() throws Exception {
    // The test is currently semi-automated 
    // The comparison value is from manually executed get request  for one user_id
    String responseResult = "17";

    String sqlQueryForTarget =
        "select coalesce(total_followers,0) from (select total_followers,  max(inserted_time_stamp) from DP_SOCIAL_MEDIA_DEV.RawImportedTwitterPageStatistics where data_collection_run = '1' "
            + "and date = CURDATE() and property_id = '-1' group by total_followers) a";

    String outcomeTarget =
        dbHelper.targetTemplate().queryForObject(sqlQueryForTarget, String.class);

    assertThat("There is a problem with the row for noon import.", outcomeTarget,
        is(responseResult));

    logger.info("The total number of followers at noon imported to stage is: ", outcomeTarget);
  }
}
