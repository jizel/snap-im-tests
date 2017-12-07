package travel.snapshot.dp.qa.nonpms.etl.test.recovery;


import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.getCurrentTimestamp;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.getFireTimeForMidnight;

import org.junit.Test;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;

import java.time.LocalDateTime;

public abstract class AbstractReloadDataRecoveryEtlTest extends AbstractDataRecoveryEtlTest {

    @Test(timeout = 60000)
    public void testEtlDataRecovery() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        setAffectedDates(asList(DAY2, DAY3, DAY4, DAY5));
        start(() -> SchedulerMessage.builder().fireTime(getFireTimeForMidnight(DAY5, getTimezone())));
        checkNotifications();

        checkReloadedData(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlDataRecoveryForCustomIntegration() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        setAffectedDates(asList(DAY2, DAY3, DAY4, DAY5));
        startForAffectedProperties(id -> SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(id)
                .integrationDate(DAY5));
        checkNotifications();

        checkReloadedData(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlDataRecoveryForPastCustomIntegration() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        setAffectedDate(DAY2);
        startForAffectedProperties(id -> SchedulerMessage.builder().fireTime(IGNORED_FIRE_TIME).propertyId(id).integrationDate(DAY2).isCurrentDay(false));
        checkNotifications();

        checkReloadedData(singletonList(DAY2), start);
    }

    @Test(timeout = 60000)
    public void testEtlDataRecoveryForPastCustomIntegrationWithOverrideData() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);
        insertData(DAY2);

        setAffectedDate(DAY2);
        startForAffectedProperties(id -> SchedulerMessage.builder()
                .fireTime(IGNORED_FIRE_TIME)
                .propertyId(id)
                .integrationDate(DAY2)
                .overrideData(true));
        checkNotifications();

        checkReloadedData(singletonList(DAY2), start);
    }

}
