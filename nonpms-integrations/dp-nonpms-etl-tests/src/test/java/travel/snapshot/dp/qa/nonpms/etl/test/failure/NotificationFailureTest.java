package travel.snapshot.dp.qa.nonpms.etl.test.failure;

import static java.util.Collections.emptyList;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.messages.FailureNotification.ErrorType;
import static travel.snapshot.dp.qa.nonpms.etl.messages.FailureNotification.ErrorType.AUTHORIZATION_PROBLEM;
import static travel.snapshot.dp.qa.nonpms.etl.messages.FailureNotification.ErrorType.SERVICE_DOWN;
import static travel.snapshot.dp.qa.nonpms.etl.messages.FailureNotification.ErrorType.UNEXPECTED_ERROR;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Notification.failureNotification;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.wrapException;
import static travel.snapshot.dp.qa.nonpms.etl.util.JsonConverter.convertToJson;

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
import travel.snapshot.dp.qa.nonpms.etl.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.etl.messages.FailureNotification;
import travel.snapshot.dp.qa.nonpms.etl.util.Jms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.jms.Message;

/**
 * This test just listens on notification.integration_failure.topic and fails if it finds any message there.
 *
 * The first test fetches all failure notifications. A test checks for failure notifications of required error type.
 * Not optimal solution as all tests must be run at once. Better solution would be to select notifications on JMS level.
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

    private static Map<ErrorType, List<FailureNotification>> FAILURE_NOTIFICATION_CACHE = null;

    @Test
    public void noAuthorizationProblems() {
        checkFailureNotifications(AUTHORIZATION_PROBLEM);
    }

    @Test
    public void noServiceDown() {
        checkFailureNotifications(SERVICE_DOWN);
    }

    @Test
    public void noUnexpectedErrors() {
        checkFailureNotifications(UNEXPECTED_ERROR);
    }

    private void checkFailureNotifications(ErrorType errorType) {
        List<FailureNotification> failureNotifications = getValues(getFailureNotificationCache(), errorType);
        assertThat(failureNotifications).withFailMessage(convertToString(failureNotifications)).isEmpty();
    }

    private synchronized Map<ErrorType, List<FailureNotification>> getFailureNotificationCache() {
        if (!isNull(FAILURE_NOTIFICATION_CACHE)) {
            return FAILURE_NOTIFICATION_CACHE;
        }

        List<FailureNotification> failureNotifications = consumeAllIntegrationFailureNotifications(getFailureNotificationQueue());
        FAILURE_NOTIFICATION_CACHE = synchronizedMap(failureNotifications.stream().collect(groupingBy(FailureNotification::getErrorType)));

        return FAILURE_NOTIFICATION_CACHE;
    }

    private static <K, V> List<V> getValues(Map<K, List<V>> map, K key) {
        return map.getOrDefault(key, emptyList());
    }

    private static <T> String convertToString(List<T> list) {
        return list.stream().map(Object::toString).collect(joining("\n", "\n", ""));
    }

    private List<FailureNotification> consumeAllIntegrationFailureNotifications(String destination) {
        List<FailureNotification> allFailureNotifications = new ArrayList<>();
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
