package travel.snapshot.dp.qa.helpers;

import com.mifmif.common.regex.Generex;

import org.json.JSONObject;

import java.util.Iterator;

public class RegexValueConverter {

    public static final String REGEX_START_CHAR = "\\";

    public static JSONObject transform(String json) {

        JSONObject data = new JSONObject();
        JSONObject obj = new JSONObject(json);
        Iterator<?> keys = obj.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Object value = obj.get(key);
            if (value.toString().startsWith(REGEX_START_CHAR)) {
                Generex generex = new Generex(value.toString());
                data.put(key, generex.random());
                continue;
            }
            data.put(key,value);
        }
        return data;
    }

    public static JSONObject transform(JSONObject jsonObject) {
        return RegexValueConverter.transform(jsonObject.toString());
    }
}