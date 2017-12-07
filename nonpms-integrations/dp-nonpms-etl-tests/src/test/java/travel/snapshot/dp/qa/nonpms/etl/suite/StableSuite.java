package travel.snapshot.dp.qa.nonpms.etl.suite;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import travel.snapshot.dp.qa.nonpms.etl.test.basic.FacebookEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.basic.GoogleAnalyticsEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.basic.InstagramEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.basic.TripAdvisorEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.basic.TwitterEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.FacebookDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.GoogleAnalyticsDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.InstagramDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.TripAdvisorDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.TwitterDataRecoveryEtlTest;

@Ignore("Test suite is used for manual run in IDE")
@RunWith(Suite.class)
@SuiteClasses({
        FacebookEtlTest.class,
        InstagramEtlTest.class,
        TwitterEtlTest.class,
        GoogleAnalyticsEtlTest.class,
        TripAdvisorEtlTest.class,
        InstagramDataRecoveryEtlTest.class,
        FacebookDataRecoveryEtlTest.class,
        TwitterDataRecoveryEtlTest.class,
        TripAdvisorDataRecoveryEtlTest.class,
        GoogleAnalyticsDataRecoveryEtlTest.class
})
public class StableSuite {

    @BeforeClass
    public static void suite() {
        System.setProperty("spring.profiles.active", "stable");
    }
}
