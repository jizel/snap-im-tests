package travel.snapshot.qa.connection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;

@Category(UnitTest.class)
public class ConnectionCheckBuilderTestCase {

    private final ConnectionCheck.Builder builder = new ConnectionCheck.Builder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void negativeTimeoutTest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Specified timeout was lower then 1: 0");

        builder.timeout(0);
    }

    @Test
    public void negativeReexecutionIntervalTest() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Specified reexecutionInterval was lower then 1: 0");

        builder.reexecutionInterval(0);
    }

    @Test
    public void timeoutLowerOrEqualtoReexecutionIntervalTest() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Timeout (10) is lower or equals to reexecution interval (20).");

        builder.timeout(10).reexecutionInterval(20).build();
    }

    @Test
    public void timeoutBiggerOrEqualtoReexecutionIntervalTest() {
        builder.timeout(40).reexecutionInterval(20).build();
    }
}
