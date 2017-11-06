package travel.snapshot.dp.qa.nonpms.dwh.suite;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import travel.snapshot.dp.qa.nonpms.dwh.test.FacebookDwhTest;
import travel.snapshot.dp.qa.nonpms.dwh.test.GoogleAnalyticsDwhTest;
import travel.snapshot.dp.qa.nonpms.dwh.test.InstagramDwhTest;
import travel.snapshot.dp.qa.nonpms.dwh.test.TripAdvisorDwhTest;
import travel.snapshot.dp.qa.nonpms.dwh.test.TwitterDwhTest;

@Ignore("Test suite is used for manual run in IDE")
@RunWith(Suite.class)
@SuiteClasses({
        FacebookDwhTest.class,
        InstagramDwhTest.class,
        TwitterDwhTest.class,
        GoogleAnalyticsDwhTest.class,
        TripAdvisorDwhTest.class
})
public class StableSuite {

    @BeforeClass
    public static void suite() {
        System.setProperty("spring.profiles.active", "stable");
    }
}
