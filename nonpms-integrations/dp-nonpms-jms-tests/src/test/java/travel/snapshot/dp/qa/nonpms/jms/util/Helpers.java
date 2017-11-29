package travel.snapshot.dp.qa.nonpms.jms.util;

import static travel.snapshot.dp.qa.nonpms.jms.util.DateUtils.toLocalDate;
import static travel.snapshot.dp.qa.nonpms.jms.util.JsonConverter.convertToJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import travel.snapshot.dp.qa.nonpms.jms.messages.SchedulerMessage;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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

    public static SchedulerMessage createSchedulerMessage(String fireTime, boolean overrideData) {
        return SchedulerMessage.builder()
                .fireTime(Instant.parse(fireTime))
                .overrideData(overrideData)
                .build();
    }

    public static String createSchedulerMessageJson(String fireTime) throws JsonProcessingException {
        return convertToJson(createSchedulerMessage(fireTime));
    }

    public static String createSchedulerMessageJson(String fireTime, String propertyId, String integrationDate)
            throws JsonProcessingException {
        return convertToJson(createSchedulerMessage(fireTime, propertyId, integrationDate));
    }

    public static String createSchedulerMessageJson(String fireTime, boolean overrideData) throws JsonProcessingException {
        return convertToJson(createSchedulerMessage(fireTime, overrideData));
    }

    public static LocalDateTime extractTimestamp(Map<String, Object> map, String key) {
        return ((Timestamp)map.get(key)).toLocalDateTime();
    }

    public static LocalDate extractLocalDate(Map<String, Object> map, String key) {
        return toLocalDate(((Long)map.get(key)).intValue());
    }

}
