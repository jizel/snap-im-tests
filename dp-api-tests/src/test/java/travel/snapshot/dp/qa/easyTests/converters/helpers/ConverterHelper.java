package travel.snapshot.dp.qa.easyTests.converters.helpers;

import java.util.Map;

/**
 * Help methods for object converters
 */
public class ConverterHelper {

    public static String getStringValue(Map valueMap, String key) {
        if (valueMap.get(key) == null) {
            return null;
        }
        return String.valueOf(valueMap.get(key));
    }

    public static Boolean getBooleanValue(Map valueMap, String key) {
        if (valueMap.get(key) == null) {
            return null;
        }
        return Boolean.valueOf(getStringValue(valueMap, key));
    }

}
