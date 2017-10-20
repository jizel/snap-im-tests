package travel.snapshot.dp.qa.jms;

import static travel.snapshot.dp.qa.jms.messages.Provider.REVIEW_TRIPADVISOR;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.jms.messages.Provider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

@Getter
public class TripAdvisorEtlTest extends AbstractEtlTest {

    Provider provider = REVIEW_TRIPADVISOR;

    @Value("${integration.tripadvisor.start.queue}")
    String startQueue;
    @Value("${integration.tripadvisor.start.message} ")
    String startMessage;
    @Value("#{'${integration.tripadvisor.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.tripadvisor.timezone}")
    String timezone;

    private Instant timestamp = Instant.now();

    @Override
    protected String getAffectedDate() {
        return LocalDate.now(ZoneId.of(timezone)).toString();
    }

    @Override
    protected String getEndDate() {
        return getAffectedDate();
    }

}
