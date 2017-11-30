package travel.snapshot.dp.qa.nonpms.etl.test.failure;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
        List<FailureNotification> failureNotifications = consumeAllIntegrationFailureNotifications(getFailureNotificationQueue());

        Map<ErrorType, List<FailureNotification>> failureNotificationMap = failureNotifications.stream()
                .collect(groupingBy(FailureNotification::getErrorType));

        List<FailureNotification> authorizationProblems = getValues(failureNotificationMap, AUTHORIZATION_PROBLEM);
        List<FailureNotification> serviceDownProblems= getValues(failureNotificationMap, SERVICE_DOWN);
        List<FailureNotification> unexpectedErrors = getValues(failureNotificationMap, UNEXPECTED_ERROR);

        assertAll(
                () -> assertThat(authorizationProblems.isEmpty())
                        .withFailMessage("Authorization problems:\n" + convertToString(authorizationProblems)).isTrue(),
                () -> assertThat(serviceDownProblems.isEmpty())
                        .withFailMessage("Service down problems:\n" + convertToString(serviceDownProblems)).isTrue(),
                () -> assertThat(unexpectedErrors.isEmpty())
                        .withFailMessage("Unexpected errors:\n" + convertToString(unexpectedErrors)).isTrue()
        );
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

    private static <K, V> List<V> getValues(Map<K, List<V>> map, K key) {
        return map.getOrDefault(key, emptyList());
    }

    private static <T> String convertToString(List<T> list) {
        return list.stream().map(Object::toString).collect(joining("\n"));
    }

}
