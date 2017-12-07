package travel.snapshot.dp.qa.nonpms.etl.test;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.CollectionUtils.intersection;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Notification.etlNotification;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Notification.failureNotification;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.wrapException;
import static travel.snapshot.dp.qa.nonpms.etl.util.JsonConverter.convertToJson;

import lombok.Setter;
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
import travel.snapshot.dp.qa.nonpms.etl.config.TestConfig;
import travel.snapshot.dp.qa.nonpms.etl.messages.DateRange;
import travel.snapshot.dp.qa.nonpms.etl.messages.EtlNotification;
import travel.snapshot.dp.qa.nonpms.etl.messages.FailureNotification;
import travel.snapshot.dp.qa.nonpms.etl.messages.Notification;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;
import travel.snapshot.dp.qa.nonpms.etl.util.Jms;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.jms.Message;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Jms.class, TestConfig.class })
@ImportAutoConfiguration({ JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class })
@DirtiesContext(classMode =  DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractEtlTest {

    protected static final Instant IGNORED_FIRE_TIME = Instant.parse("2017-01-01T00:00:00Z");

    protected abstract Provider getProvider();
    protected abstract String getStartQueue();
    protected abstract Set<String> getAffectedProperties();
    protected abstract String getTimezone();

    @Setter
    private List<LocalDate> affectedDates;
    @Setter
    private Instant timestamp;

    @Autowired
    protected Jms jms;

    private BlockingQueue<Notification> notifications;
    private Set<LocalDate> processedAffectedDates;

    @Before
    public void setUp() {
        notifications = new ArrayBlockingQueue<>(10);
        processedAffectedDates = new HashSet<>();

        affectedDates = singletonList(LocalDate.now(ZoneId.of(getTimezone())));
        timestamp = Instant.now();
    }

    protected void checkNotifications() throws Exception {

        do {
            Notification notification = notifications.take();

            assertThat(notification.isFailure())
                    .withFailMessage("Received failure notification for affected property: " + notification)
                    .isFalse();

            EtlNotification etlNotification = (EtlNotification) notification;

            processedAffectedDates.add(etlNotification.getAffectedDateRanges().stream()
                    .map(DateRange::getEndDate).filter(e -> affectedDates.contains(e)).findAny()
                    .orElseThrow(IllegalStateException::new));

        } while (!processedAffectedDates.containsAll(affectedDates));

        assertThat(processedAffectedDates).containsAll(affectedDates);
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

    private Predicate<Notification> checkProvider = e -> getProvider().equals(e.getProvider());
    private Predicate<Notification> checkAffectedProperties = e -> !intersection(e.getAffectedProperties(), getAffectedProperties()).isEmpty();
    private Predicate<FailureNotification> checkAffectedDate = e -> affectedDates.contains(e.getAffectedDate());
    private Predicate<EtlNotification> checkTimestamp = e -> e.getTimestamp().toInstant().isAfter(timestamp);
    private Predicate<EtlNotification> checkEndDate = e -> e.getAffectedDateRanges().stream().map(DateRange::getEndDate)
            .anyMatch(d -> affectedDates.contains(d));

    private void putNotification(Notification notification) {
        try {
            notifications.put(notification);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setAffectedDate(LocalDate date) {
        affectedDates = singletonList(date);
    }

    protected void start(Supplier<SchedulerMessage.SchedulerMessageBuilder> schedulerMessageFactory) {
        start(getStartQueue(), schedulerMessageFactory.get().build());
    }

    protected void startForAffectedProperties(Function<String, SchedulerMessage.SchedulerMessageBuilder> schedulerMessageFactory) {
        for (String propertyId : getAffectedProperties()) {
            start(getStartQueue(), schedulerMessageFactory.apply(propertyId).build());
        }
    }

    protected void start(String startQueue, SchedulerMessage schedulerMessage) {
        wrapException(() -> jms.send(startQueue, convertToJson(schedulerMessage)));
    }

    protected void runForAffectedProperties(Consumer<String> call) {
        getAffectedProperties().forEach(call);
    }

}
