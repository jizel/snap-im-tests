package travel.snapshot.dp.qa.helpers;

import org.apache.commons.lang3.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2015-10-02T11:35:15.166+02:00")
public class StringUtil {
    /**
     * Check if the given array contains the given value (with case-insensitive comparison).
     *
     * @param array The array
     * @param value The value to search
     * @return true if the array contains the value
     */
    public static boolean containsIgnoreCase(String[] array, String value) {
        for (String str : array) {
            if (value == null && str == null) return true;
            if (value != null && value.equalsIgnoreCase(str)) return true;
        }
        return false;
    }

    /**
     * Join an array of strings with the given separator. <p> Note: This might be replaced by
     * utility method from commons-lang or guava someday if one of those libraries is added as
     * dependency. </p>
     *
     * @param array     The array of strings
     * @param separator The separator
     * @return the resulting string
     */
    public static String join(String[] array, String separator) {
        int len = array.length;
        if (len == 0) return "";

        StringBuilder out = new StringBuilder();
        out.append(array[0]);
        for (int i = 1; i < len; i++) {
            out.append(separator).append(array[i]);
        }
        return out.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    public static String toIndentedString(Object o) {
        if (o == null) return "null";
        return o.toString().replace("\n", "\n    ");
    }

    /**
     * if text is empty, returns null
     *
     * if text is date in ISO format (2015-01-01), it returns this date
     *
     * text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which
     * will add or substract particular number of days/weeks/months from first part of expression
     *
     * @return date parsed from text
     */
    public static LocalDate parseDate(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        String[] parts = text.split(" ");
        String dateText = parts[0];


        LocalDate date = null;

        if ("today".equals(dateText.trim())) {
            date = LocalDate.now();
        } else {
            date = LocalDate.parse(dateText);
        }


        if (parts.length == 1) {
            return date;
        } else {
            String sign = parts[1];
            String incrementNumber = parts[2];
            String incrementUnit = parts[3];

            TemporalUnit unitValue = null;
            Integer incrementNumberValue = Integer.valueOf(incrementNumber);
            switch (incrementUnit) {
                case ("days"):
                case ("day"): {
                    unitValue = ChronoUnit.DAYS;
                    break;
                }
                case ("months"):
                case ("month"): {
                    unitValue = ChronoUnit.MONTHS;
                    break;
                }
                case ("weeks"):
                case ("week"): {
                    unitValue = ChronoUnit.WEEKS;
                    break;
                }
            }

            if ("+".equals(sign.trim())) {
                date = date.plus(incrementNumberValue, unitValue);
            } else if ("-".equals(sign.trim())) {
                date = date.minus(incrementNumberValue, unitValue);
            } else throw new DateTimeException("Unknown sign for manipulating dates");
        }


        return date;
    }
}