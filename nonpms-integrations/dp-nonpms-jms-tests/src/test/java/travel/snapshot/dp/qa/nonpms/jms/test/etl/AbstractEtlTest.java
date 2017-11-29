package travel.snapshot.dp.qa.nonpms.jms.test.etl;

import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.jms.messages.Notification.etlNotification;
import static travel.snapshot.dp.qa.nonpms.jms.messages.Notification.failureNotification;
import static travel.snapshot.dp.qa.nonpms.jms.util.JsonConverter.convertToJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import travel.snapshot.dp.qa.nonpms.jms.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.jms.messages.DateRange;
import travel.snapshot.dp.qa.nonpms.jms.messages.EtlNotification;
import travel.snapshot.dp.qa.nonpms.jms.messages.FailureNotification;
import travel.snapshot.dp.qa.nonpms.jms.messages.Notification;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;
import travel.snapshot.dp.qa.nonpms.jms.util.Jms;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import javax.jms.Message;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Jms.class, TestConfig.class })
@ImportAutoConfiguration({ JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class })
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractEtlTest {

    protected abstract Provider getProvider();
    protected abstract String getAffectedDate();
    protected abstract Instant getTimestamp();
    protected abstract String getStartQueue();
    protected abstract String getFireTime();
    protected abstract Set<String> getAffectedProperties();
    protected abstract String getEndDate();

    @Autowired
    protected Jms jms;

    private BlockingQueue<Notification> notifications;
    private Set<String> processedProperties;

    @Before
    public void setUp() {
        notifications = new ArrayBlockingQueue<Notification>(10);
        processedProperties = new HashSet<>();
    }

    protected void checkNotifications() throws JsonProcessingException, InterruptedException {

        do {
            Notification notification = notifications.take();

            assertThat(notification.isFailure())
                    .withFailMessage("Received failure notification for affected property: " + notification)
                    .isFalse();

            processedProperties.addAll(notification.getAffectedProperties());

        } while (!processedProperties.containsAll(getAffectedProperties()));

        assertThat(processedProperties).containsAll(getAffectedProperties());
    }



    @JmsListener(destination = "${notification.integration_failure.topic}", containerFactory = "notificationListenerFactory")
    public void handleIntegrationFailureNotification(Message message) throws InterruptedException, IOException {
        String json = convertToJson(message);
        log.debug("Failure Notification: " + json);

        Optional.of(failureNotification(json))
                .filter(checkProvider)
                .filter(checkAffectedDate)
                .filter(checkAffectedProperties)
                .ifPresent(this::putNotification);
    }

    @JmsListener(destination = "${notification.etl.topic}", containerFactory = "notificationListenerFactory")
    public void handleEtlNotification(Message message) throws InterruptedException, IOException {
        String json = convertToJson(message);
        log.debug("ETL Notification: " + json);

        Optional.of(etlNotification(json))
                .filter(checkProvider)
                .filter(checkTimestamp)
                .filter(checkAffectedProperties)
                .filter(checkEndDate)
                .ifPresent(this::putNotification);
    }

    Predicate<Notification> checkProvider = e -> getProvider().equals(e.getProvider());
    Predicate<Notification> checkAffectedProperties = e -> !intersection(e.getAffectedProperties(), getAffectedProperties()).isEmpty();
    Predicate<FailureNotification> checkAffectedDate = e -> e.getAffectedDate().isEqual(LocalDate.parse(getAffectedDate()));
    Predicate<EtlNotification> checkTimestamp = e -> e.getTimestamp().toInstant().isAfter(getTimestamp());
    Predicate<EtlNotification> checkEndDate = e -> e.getAffectedDateRanges().stream().map(DateRange::getEndDate)
            .filter(d -> d.isEqual(LocalDate.parse(getEndDate()))).findAny().isPresent();

    private void putNotification(Notification notification) {
        try {
            notifications.put(notification);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
