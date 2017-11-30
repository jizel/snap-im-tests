package travel.snapshot.dp.qa.nonpms.etl.test.extra;

import static java.lang.String.format;
import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

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
import travel.snapshot.dp.qa.nonpms.etl.util.Jms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Jms.class, TestConfig.class})
@ImportAutoConfiguration({JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AllTimeZonesEtlTest {

    private static final LocalDate AFFECTED_DATE = LocalDate.parse("2017-11-20");

    @Autowired
    Jms jms;

    @Value("${integration.twitter.start.queue}")
    String twitterStartQueue;
    @Value("${integration.facebook.start.queue}")
    String facebookStartQueue;
    @Value("${integration.googleanalytics.start.queue}")
    String googleAnalstartQueue;
    @Value("${integration.instagram.start.queue}")
    String instagramStartQueue;
    @Value("${integration.tripadvisor.start.queue}")
    String tripAdvisorstartQueue;

    @Test
    public void sendStartAllTimezonesTwitter() {
        generateStartMsgs().forEach(msg -> jms.send(twitterStartQueue, msg));
    }

    @Test
    public void sendStartAllTimezonesFacebook() {
        generateStartMsgs().forEach(msg -> jms.send(facebookStartQueue, msg));
    }

    @Test
    public void sendStartAllTimezonesGoogleAnalytics() {
        generateStartMsgs().forEach(msg -> jms.send(googleAnalstartQueue, msg));
    }

    @Test
    public void sendStartAllTimezonesInstagram() {
        generateStartMsgs().forEach(msg -> jms.send(instagramStartQueue, msg));
    }

    @Test
    public void sendStartAllTimezonesTripAdvisor() {
        generateStartMsgs().forEach(msg -> jms.send(tripAdvisorstartQueue, msg));
    }

    private List<String> generateStartMsgs() {
        List<String> startMessages = range(0, 24)
                .mapToObj(i -> LocalTime.of(i, 0))
                .map(e -> LocalDateTime.of(AFFECTED_DATE, e))
                .map(e -> e.toInstant(UTC))
                .map(e -> format("{\"fireTime\":\"%s\"}", e.toString()))
                .collect(toList());

        log.debug("All start messages: " + startMessages.toString());

        return startMessages;
    }

}
