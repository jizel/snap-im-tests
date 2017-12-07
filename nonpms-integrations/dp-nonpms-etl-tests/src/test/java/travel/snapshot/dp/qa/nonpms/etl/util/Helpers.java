package travel.snapshot.dp.qa.nonpms.etl.util;

import static java.time.LocalTime.MIDNIGHT;
import static travel.snapshot.dp.qa.nonpms.etl.util.DateUtils.toLocalDate;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        return JsonConverter.convertToJson(createSchedulerMessage(fireTime));
    }

    public static String createSchedulerMessageJson(String fireTime, String propertyId, String integrationDate)
            throws JsonProcessingException {
        return JsonConverter.convertToJson(createSchedulerMessage(fireTime, propertyId, integrationDate));
    }

    public static String createSchedulerMessageJson(String fireTime, boolean overrideData) throws JsonProcessingException {
        return JsonConverter.convertToJson(createSchedulerMessage(fireTime, overrideData));
    }

    public static LocalDateTime extractTimestamp(Map<String, Object> map, String key) {
        return ((Timestamp)map.get(key)).toLocalDateTime();
    }

    public static LocalDate extractLocalDate(Map<String, Object> map, String key) {
        return toLocalDate(((Long)map.get(key)).intValue());
    }

    public static Instant getCurrentFireTimeForMidnight(String timezone) {
        return ZonedDateTime.now(ZoneId.of(timezone)).plusDays(1).with(MIDNIGHT).toInstant();
    }

    public static LocalDate getCurrentDate(String timezone) {
        return LocalDate.now(ZoneId.of(timezone));
    }

    public static Instant getFireTimeForMidnight(LocalDate date, String timezone) {
        return date.plusDays(1).atStartOfDay(ZoneId.of(timezone)).toInstant();
    }

    public static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now(Clock.systemUTC());
    }

}
