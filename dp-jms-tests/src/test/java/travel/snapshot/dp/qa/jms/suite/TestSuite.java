package travel.snapshot.dp.qa.jms.suite;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import travel.snapshot.dp.qa.jms.FacebookEtlTest;
import travel.snapshot.dp.qa.jms.GoogleAnalyticsEtlTest;
import travel.snapshot.dp.qa.jms.InstagramEtlTest;
import travel.snapshot.dp.qa.jms.TripAdvisorEtlTest;
import travel.snapshot.dp.qa.jms.TwitterEtlTest;

@Ignore("Test suite is used for manual run in IDE")
@RunWith(Suite.class)
@SuiteClasses({
        FacebookEtlTest.class,
        InstagramEtlTest.class,
        TwitterEtlTest.class,
        GoogleAnalyticsEtlTest.class,
        TripAdvisorEtlTest.class

})
public class TestSuite {

    @BeforeClass
    public static void suite() {
        System.setProperty("spring.profiles.active", "test");
    }
}
