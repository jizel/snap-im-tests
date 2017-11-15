package travel.snapshot.dp.qa.nonpms.jms.test.failure;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.jms.messages.Notification.failureNotification;
import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.wrapException;
import static travel.snapshot.dp.qa.nonpms.jms.util.JsonConverter.convertToJson;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import travel.snapshot.dp.qa.nonpms.jms.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.jms.messages.Notification;
import travel.snapshot.dp.qa.nonpms.jms.util.Jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jms.Message;

/**
 * This test just listens on notification.integration_failure.topic and fails if it finds any message there.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jms.class, TestConfig.class})
@ImportAutoConfiguration({JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class NotificationFailureTest {

    @Getter
    @Value("${notification.integration_failure.queue}")
    private String failureNotificationQueue;
    @Autowired
    private Jms jms;

    private static final int RECEIVE_TIMEOUT = 1000;

    @Test
    public void testNoIntegrationFailureNotifications() {
        List<Notification> failureNotifications = consumeAllIntegrationFailureNotifications(getFailureNotificationQueue());
        assertThat(failureNotifications.isEmpty())
                .withFailMessage("There were some failure notifications: " + failureNotifications)
                .isTrue();
    }

    private List<Notification> consumeAllIntegrationFailureNotifications(String destination) {
        List<Notification> allFailureNotifications = new ArrayList<>();
        Message message;
        do {
            message = jms.receive(destination, RECEIVE_TIMEOUT);
            Optional.ofNullable(message).ifPresent(msg -> {
                String json = convertToJson(msg);
                log.warn("Received failure notification(s): " + json);
                wrapException(() -> allFailureNotifications.add(failureNotification(json)));
            });
        } while (!isNull(message));

        return allFailureNotifications;
    }

}
