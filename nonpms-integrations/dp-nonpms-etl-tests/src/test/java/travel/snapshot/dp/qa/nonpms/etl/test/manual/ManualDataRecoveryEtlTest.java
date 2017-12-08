package travel.snapshot.dp.qa.nonpms.etl.test.manual;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_FACEBOOK;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.WEBPERFORMANCE_GOOGLEANALYTICS;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.isValidLocalDate;

import lombok.Getter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;
import travel.snapshot.dp.qa.nonpms.etl.test.AbstractEtlTest;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class ManualDataRecoveryEtlTest extends AbstractEtlTest {

    @Value("${integration.facebook.start.queue}")
    String facebookStartQueue;
    @Value("${integration.googleanalytics.start.queue}")
    String googleAnalyticsStartQueue;

    @Value("#{'${integration.manual.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.manual.integration.date}")
    String integrationDate;

    Provider provider;
    String startQueue;

    // Not used
    String timezone = "UTC";

    @Test
    public void facebookCustomIntegration() throws Exception {
        customIntegration(SOCIALMEDIA_FACEBOOK, facebookStartQueue);
    }

    @Test
    public void googleAnalyticsCustomIntegration() throws Exception {
        customIntegration(WEBPERFORMANCE_GOOGLEANALYTICS, googleAnalyticsStartQueue);
    }

    private void customIntegration(Provider provider, String startQueue) throws Exception {
        assertThat(getAffectedProperties()).withFailMessage("Missing property ID!").isNotEmpty();
        assertThat(isValidLocalDate(this.integrationDate)).withFailMessage("Invalid integration date!").isTrue();

        this.provider = provider;
        LocalDate integrationDate = LocalDate.parse(this.integrationDate);

        setAffectedDate(integrationDate);
        runForAffectedProperties(id -> start(startQueue, createSchedulerMessage(id, integrationDate)));
        checkNotifications();
    }

    private static SchedulerMessage createSchedulerMessage(String propertyId, LocalDate integrationDate) {
        return SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(propertyId)
                .integrationDate(integrationDate)
                .build();
    }

}
