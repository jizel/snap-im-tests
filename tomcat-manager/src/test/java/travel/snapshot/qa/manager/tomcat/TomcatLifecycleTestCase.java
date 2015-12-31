package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.Spacelift;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter;
import travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper;

@RunWith(JUnit4.class)
public class TomcatLifecycleTestCase {

    @Test
    public void startAndStopTomcatVerbose() {

        TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder().build();

        TomcatManager tomcatManager = Spacelift.task(configuration, TomcatStarter.class).execute().await();

        Spacelift.task(tomcatManager, TomcatStopper.class).execute().await();
    }

    @Test
    public void startAndStopTomcatMinimal() {
        Spacelift.task(TomcatStarter.class).then(TomcatStopper.class).execute().await();
    }

    @Test
    public void isContainerRunningWithStoppedContainerTest() {
        Assert.assertFalse(new TomcatManager().isRunning());
    }

    @Test
    public void isContainerRunningWithStartedContainer() {
        TomcatManager tomcatManager = Spacelift.task(TomcatStarter.class).execute().await();

        Assert.assertTrue(tomcatManager.isRunning());

        Spacelift.task(tomcatManager, TomcatStopper.class).execute().await();
    }
}
