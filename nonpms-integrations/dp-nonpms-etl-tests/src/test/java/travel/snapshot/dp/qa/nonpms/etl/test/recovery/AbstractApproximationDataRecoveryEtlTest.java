package travel.snapshot.dp.qa.nonpms.etl.test.recovery;


import org.junit.Test;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

public abstract class AbstractApproximationDataRecoveryEtlTest extends AbstractDataRecoveryEtlTest {

    @Test(timeout = 60000)
    public void testEtlForDataRecovery() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        start(() -> SchedulerMessage.builder().fireTime(createFireTimeForMidnight(DAY5, getTimezone())));

        checkApproximatedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlForDataRecoveryForCustomIntegration() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        startForAffectedProperties(id -> SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(id)
                .integrationDate(DAY5));

        checkApproximatedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    private void checkApproximatedDays(List<LocalDate> days, LocalDateTime timestamp) throws Exception {
        checkNotificationForDay(DAY5);

        for (LocalDate day : days) {
            affectedDate = day.toString();
            checkReloadedData(day, timestamp);
        }
    }
}
