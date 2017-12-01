package travel.snapshot.dp.qa.nonpms.etl.test.recovery;


import org.junit.Test;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;

public abstract class AbstractReloadDataRecoveryEtlTest extends AbstractDataRecoveryEtlTest {

    @Test(timeout = 60000)
    public void testEtlForDataRecovery() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        start(() -> SchedulerMessage.builder().fireTime(createFireTimeForMidnight(DAY5, getTimezone())));

        checkReloadedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlForDataRecoveryForCustomIntegration() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);
        startForAffectedProperties(id -> SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(id)
                .integrationDate(DAY5));

        checkReloadedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlDataRecoveryForPastCustomIntegration() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        startForAffectedProperties(id -> SchedulerMessage.builder().fireTime(IGNORED_FIRE_TIME).propertyId(id).integrationDate(DAY2).isCurrentDay(false));

        checkReloadedDays(asList(DAY2), start);
    }

    @Test(timeout = 60000)
    public void testEtlDataRecoveryForPastCustomIntegrationWithOverrideData() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);
        insertData(DAY2);

        startForAffectedProperties(id -> SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(id)
                .integrationDate(DAY2)
                .isCurrentDay(false)
                .overrideData(true));

        checkReloadedDays(asList(DAY2), start);
    }

}
