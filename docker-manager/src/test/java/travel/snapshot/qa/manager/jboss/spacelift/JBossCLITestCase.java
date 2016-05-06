package travel.snapshot.qa.manager.jboss.spacelift;

import static org.mockito.MockitoAnnotations.initMocks;

import org.arquillian.spacelift.Spacelift;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.jboss.api.JBossContainerManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JVM;
import travel.snapshot.qa.manager.jboss.configuration.Util;

import java.util.HashMap;

@Category(JBossTest.class)
public class JBossCLITestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
            .setJVM(new JVM.Builder().setJBossHome(Util.getJBossHome()).build())
            .build();

    private JBossCLI jBossCLI = Spacelift.task(JBossCLI.class)
            .controllerHost("127.0.0.1")
            .controllerPort(9990)
            .environment(new HashMap<String, String>() {{
                put("JBOSS_HOME", Util.getJBossHome());
            }})
            .user("admin")
            .password("admin")
            .connect()
            .cliCommand("quit")
            .shouldExitWith(0);

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testJBossCLIexecution() throws Exception {
        JBossContainerManager manager = Spacelift.task(configuration, JBossStandaloneStarter.class).execute().await();

        jBossCLI.execute();

        Spacelift.task(manager, JBossStopper.class).execute().await();
    }
}
