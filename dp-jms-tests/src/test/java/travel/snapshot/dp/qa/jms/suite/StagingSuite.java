package travel.snapshot.dp.qa.jms.suite;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import travel.snapshot.dp.qa.jms.test.etl.FacebookEtlTest;
import travel.snapshot.dp.qa.jms.test.etl.GoogleAnalyticsEtlTest;
import travel.snapshot.dp.qa.jms.test.etl.InstagramEtlTest;
import travel.snapshot.dp.qa.jms.test.etl.TripAdvisorEtlTest;
import travel.snapshot.dp.qa.jms.test.etl.TwitterEtlTest;

@Ignore("Test suite is used for manual run in IDE")
@RunWith(Suite.class)
@SuiteClasses({
        FacebookEtlTest.class,
        InstagramEtlTest.class,
        TwitterEtlTest.class,
        GoogleAnalyticsEtlTest.class,
        TripAdvisorEtlTest.class

})
public class StagingSuite {

    @BeforeClass
    public static void suite() {
        System.setProperty("spring.profiles.active", "staging");
    }
}
