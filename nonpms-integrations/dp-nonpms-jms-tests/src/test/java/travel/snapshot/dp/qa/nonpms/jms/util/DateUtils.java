package travel.snapshot.dp.qa.nonpms.jms.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtils {

    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static int localDateToDbDateId(@NonNull LocalDate date) {
        return Integer.parseInt(date.format(DB_DATE_FORMATTER));
    }

    public static int localDateToDbDateId(@NonNull String date) {
        return localDateToDbDateId(LocalDate.parse(date));
    }

}
