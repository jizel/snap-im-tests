package travel.snapshot.dp.qa.helpers;

import org.json.JSONObject;

import java.util.Iterator;

public class NullStringObjectValueConverter {

    public static final String NULL_KEYWORD = "/null";

    public static JSONObject transform(String json) {

        JSONObject data = new JSONObject();
        JSONObject obj;
        obj = new JSONObject(json);
        Iterator<?> keys = obj.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = obj.get(key);

            if ((value != null) && !(value.toString().equalsIgnoreCase("null")) && (!value.equals(NULL_KEYWORD))) {
                data.put(key, value);
            }
        }
        return data;
    }

    public static JSONObject transform(JSONObject json) {
        return NullStringObjectValueConverter.transform(json.toString());
    }
}