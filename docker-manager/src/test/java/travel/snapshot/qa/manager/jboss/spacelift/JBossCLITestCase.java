package travel.snapshot.qa.manager.jboss.spacelift;

import static travel.snapshot.qa.manager.Util.getJBossHome;
import static travel.snapshot.qa.manager.jboss.configuration.ContainerType.WILDFLY;

import org.arquillian.spacelift.Spacelift;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.jboss.api.JBossContainerManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JVM;

import java.util.HashMap;

@Category(JBossTest.class)
public class JBossCLITestCase {

    private JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(getJBossHome(WILDFLY)).build())
            .build();

    private JBossCLI jBossCLI = Spacelift.task(JBossCLI.class)
            .controllerHost("127.0.0.1")
            .controllerPort(9990)
            .environment(new HashMap<String, String>() {{
                put("JBOSS_HOME", getJBossHome(WILDFLY));
            }})
            .user("admin")
            .password("admin")
            .connect()
            .cliCommand("quit")
            .shouldExitWith(0);

    @Test
    public void testJBossCLIexecution() throws Exception {
        JBossContainerManager manager = Spacelift.task(configuration, JBossStandaloneStarter.class).execute().await();

        jBossCLI.execute();

        Spacelift.task(manager, JBossStopper.class).execute().await();
    }
}
