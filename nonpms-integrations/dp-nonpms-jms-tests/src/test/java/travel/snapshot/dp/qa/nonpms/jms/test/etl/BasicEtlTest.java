package travel.snapshot.dp.qa.nonpms.jms.test.etl;

import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.createSchedulerMessageJson;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import travel.snapshot.dp.qa.nonpms.jms.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.jms.util.Jms;

import java.io.IOException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Jms.class, TestConfig.class })
@ImportAutoConfiguration({ JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class })
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BasicEtlTest extends AbstractEtlTest {

    private static final String IGNORED_FIRE_TIME = "2017-01-01T00:00:00Z";

    @Test(timeout = 60000)
    public void testEtl() throws InterruptedException, IOException {
        jms.send(getStartQueue(), createSchedulerMessageJson(getFireTime()));

        checkNotifications();
    }

    @Test(timeout = 60000)
    public void testEtlForSelectedProperties() throws InterruptedException, IOException {
        for (String propertyId : getAffectedProperties()) {
            jms.send(getStartQueue(), createSchedulerMessageJson(IGNORED_FIRE_TIME, propertyId, getAffectedDate()));
        }

        checkNotifications();
    }

}
