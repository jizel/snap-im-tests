package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.Spacelift;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.TomcatTest;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper;

@Category(TomcatTest.class)
public class TomcatLifecycleTestCase {

    private final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder()
            .setBindAddress("127.0.0.1")
            .build();

    @Test
    public void startAndStopTomcatVerbose() {
        TomcatManager tomcatManager = Spacelift.task(configuration, TomcatStarter.class).execute().await();

        Spacelift.task(tomcatManager, TomcatStopper.class).execute().await();
    }

    @Test
    public void startAndStopTomcatMinimal() {
        Spacelift.task(configuration, TomcatStarter.class).then(TomcatStopper.class).execute().await();
    }

    @Test
    public void isContainerRunningWithStoppedContainerTest() {
        final TomcatManager manager = new TomcatManager(configuration);

        Assert.assertFalse(manager.isRunning());
    }

    @Test
    public void isContainerRunningWithStartedContainer() {
        TomcatManager tomcatManager = Spacelift.task(configuration, TomcatStarter.class).execute().await();
        Assert.assertTrue(tomcatManager.isRunning());
        Spacelift.task(tomcatManager, TomcatStopper.class).execute().await();
    }
}
