package travel.snapshot.dp.qa.nonpms.etl.suite;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import travel.snapshot.dp.qa.nonpms.etl.test.FacebookEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.GoogleAnalyticsEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.InstagramEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.TripAdvisorEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.TwitterEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.FacebookDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.InstagramDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.TripAdvisorDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.TwitterDataRecoveryEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.recovery.WebPerformanceDataRecoveryEtlTest;

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
        WebPerformanceDataRecoveryEtlTest.class
})
public class TestSuite {

    @BeforeClass
    public static void suite() {
        System.setProperty("spring.profiles.active", "test");
    }
}
