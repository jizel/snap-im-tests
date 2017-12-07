package travel.snapshot.dp.qa.nonpms.etl.test.extra;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_FACEBOOK;

import lombok.Getter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;
import travel.snapshot.dp.qa.nonpms.etl.test.AbstractEtlTest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Getter
public class FacebookDstEtlTest extends AbstractEtlTest {

    private final Instant FIRE_TIME_FOR_WINTER_TIME_ADJUSMENT = Instant.parse("2017-11-06T01:00:00Z");
    private final LocalDate AFFECTED_DATE_FOR_WINTER_TIME_ADJUSMENT = LocalDate.parse("2017-11-05");

    private final Instant FIRE_TIME_FOR_SUMMER_TIME_ADJUSMENT = Instant.parse("2017-03-13T01:00:00Z");
    private final LocalDate AFFECTED_DATE_FOR_SUMMER_TIME_ADJUSMENT = LocalDate.parse("2017-03-12");

    Provider provider = SOCIALMEDIA_FACEBOOK;

    @Value("${integration.facebook.start.queue}")
    String startQueue;
    @Value("#{'${integration.facebook.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.facebook.timezone}")
    String timezone;

    @Test(timeout = 60000)
    public void testEtlWinterTimeAdjusment() throws Exception {
        setAffectedDate(AFFECTED_DATE_FOR_SUMMER_TIME_ADJUSMENT);
        start(() -> SchedulerMessage.builder().fireTime(FIRE_TIME_FOR_SUMMER_TIME_ADJUSMENT));
        checkNotifications();
    }

    @Test(timeout = 60000)
    public void testEtlSummerTimeAdjusment() throws Exception {
        setAffectedDate(AFFECTED_DATE_FOR_SUMMER_TIME_ADJUSMENT);
        start(() -> SchedulerMessage.builder().fireTime(FIRE_TIME_FOR_SUMMER_TIME_ADJUSMENT));
        checkNotifications();
    }

}

