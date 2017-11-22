package travel.snapshot.dp.qa.nonpms.jms.util;

import static travel.snapshot.dp.qa.nonpms.jms.util.JsonConverter.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import travel.snapshot.dp.qa.nonpms.jms.messages.SchedulerMessage;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Helper methods for dp jms tests
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Helpers {

    public static void wrapException(Callable method) {
        try {
            method.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SchedulerMessage createSchedulerMessage(String fireTime) {
        return SchedulerMessage.builder().fireTime(Instant.parse(fireTime)).build();
    }

    public static SchedulerMessage createSchedulerMessage(String fireTime, String propertyId, String integrationDate) {
        return SchedulerMessage.builder()
                .fireTime(Instant.parse(fireTime))
                .propertyId(propertyId)
                .integrationDate(LocalDate.parse(integrationDate))
                .build();
    }

    public static String createSchedulerMessageJson(String fireTime) throws JsonProcessingException {
        return convertToJson(createSchedulerMessage(fireTime));
    }

    public static String createSchedulerMessageJson(String fireTime, String propertyId, String integrationDate)
            throws JsonProcessingException {
        return convertToJson(createSchedulerMessage(fireTime, propertyId, integrationDate));
    }

}
