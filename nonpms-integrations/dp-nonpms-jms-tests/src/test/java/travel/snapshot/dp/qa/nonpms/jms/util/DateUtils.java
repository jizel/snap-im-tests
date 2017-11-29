package travel.snapshot.dp.qa.nonpms.jms.util;

import static java.time.LocalDate.from;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static int toDateId(@NonNull LocalDate date) {
        return Integer.parseInt(date.format(DB_DATE_FORMATTER));
    }

    public static int toDateId(@NonNull String date) {
        return toDateId(LocalDate.parse(date));
    }

    public static LocalDate toLocalDate(int dateId) {
        return from(DB_DATE_FORMATTER.parse(Integer.toString(dateId)));
    }

}
