package travel.snapshot.dp.qa.nonpms.jms.test.etl.extra;

import static travel.snapshot.dp.qa.nonpms.jms.messages.Provider.SOCIALMEDIA_FACEBOOK;
import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.createSchedulerMessageJson;

import lombok.Getter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;
import travel.snapshot.dp.qa.nonpms.jms.test.etl.AbstractEtlTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

@Getter
public class FacebookDstEtlTest extends AbstractEtlTest {

    private final String FIRE_TIME_FOR_WINTER_TIME_ADJUSMENT = "2017-11-06T01:00:00Z";
    private final String AFFECTED_DATE_FOR_WINTER_TIME_ADJUSMENT = "2017-11-05";

    private final String FIRE_TIME_FOR_SUMMER_TIME_ADJUSMENT = "2017-03-13T01:00:00Z";
    private final String AFFECTED_DATE_FOR_SUMMER_TIME_ADJUSMENT = "2017-03-12";

    Provider provider = SOCIALMEDIA_FACEBOOK;

    @Value("${integration.facebook.start.queue}")
    String startQueue;
    @Value("${integration.facebook.start.message} ")
    String startMessage;
    @Value("#{'${integration.facebook.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
//	@Value("${integration.facebook.end-date}")
//	String endDate;
    @Value("${integration.facebook.timezone}")
    String timezone;

    private Instant timestamp = Instant.now();

    private String fireTime = null;
    private String affectedDate = null;

    // Workaround for DPNP-127
    @Override
    protected String getEndDate() {
        return LocalDate.now(ZoneId.of(timezone)).toString();
    }

    @Test(timeout = 60000)
    public void testEtlWinterTimeAdjusment() throws Exception {
        fireTime = FIRE_TIME_FOR_WINTER_TIME_ADJUSMENT;
        affectedDate = AFFECTED_DATE_FOR_WINTER_TIME_ADJUSMENT;

        jms.send(getStartQueue(), createSchedulerMessageJson(getFireTime()));

        checkNotifications();
    }

    @Test(timeout = 60000)
    public void testEtlSummerTimeAdjusment() throws Exception {
        fireTime = FIRE_TIME_FOR_SUMMER_TIME_ADJUSMENT;
        affectedDate = FIRE_TIME_FOR_SUMMER_TIME_ADJUSMENT;

        jms.send(getStartQueue(), createSchedulerMessageJson(getFireTime()));

        checkNotifications();
    }

}

