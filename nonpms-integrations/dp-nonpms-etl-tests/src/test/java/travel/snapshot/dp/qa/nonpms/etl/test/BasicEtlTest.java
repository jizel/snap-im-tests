package travel.snapshot.dp.qa.nonpms.etl.test;

import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.createFireTimeForMidnight;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import travel.snapshot.dp.qa.nonpms.etl.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;
import travel.snapshot.dp.qa.nonpms.etl.util.Jms;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Jms.class, TestConfig.class })
@ImportAutoConfiguration({ JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class })
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class BasicEtlTest extends AbstractEtlTest {

    @Test(timeout = 60000)
    public void testEtl() throws Exception {
        start(() -> SchedulerMessage.builder()
                .fireTime(createFireTimeForMidnight(getAffectedDate(), getTimezone()))
                .isCurrentDay(true));

        checkNotifications();
    }

    @Test(timeout = 60000)
    public void testEtlForSelectedProperties() throws Exception {

        startForAffectedProperties(id -> SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(id)
                .integrationDate(getAffectedDate()));

        checkNotifications();
    }

}
