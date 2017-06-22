package travel.snapshot.dp.qa.cucumber.helpers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by sedlacek on 11/23/2015.
 */
public class DateUtils {

    public static String isoDatefromDate(Date date) {
        return fromDate(date).format(DateTimeFormatter.ISO_DATE);
    }

    public static LocalDate fromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
