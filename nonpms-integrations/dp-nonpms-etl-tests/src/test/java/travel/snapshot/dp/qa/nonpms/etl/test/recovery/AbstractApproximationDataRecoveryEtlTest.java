package travel.snapshot.dp.qa.nonpms.etl.test.recovery;


import static java.util.Arrays.asList;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.createFireTimeForMidnight;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.getCurrentTimestamp;

import org.junit.Test;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractApproximationDataRecoveryEtlTest extends AbstractDataRecoveryEtlTest {

    @Test(timeout = 60000)
    public void testEtlDataRecovery() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        start(() -> SchedulerMessage.builder().fireTime(createFireTimeForMidnight(DAY5, getTimezone())));

        checkApproximatedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlDataRecoveryForCustomIntegration() throws Exception {
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
            affectedDate = day;
            checkReloadedData(day, timestamp);
        }
    }
}
