package travel.snapshot.dp.qa.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by sedlacek on 9/30/2015.
 */
public class PropertiesHelper {


    private static boolean initializedFromFile = false;

    public static String getProperty(String key) {
        Properties props = new Properties();
        InputStream stream = PropertiesHelper.class
                .getResourceAsStream("/dp.properties");
        try {
            props.load(stream);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return props.getProperty(key);
    }

    public static Integer[] getListOfInt(String key) {
        String arrayString = getProperty(key);

        return Arrays.stream(arrayString.split(",")).filter(s -> s != null && !"".equals(s)).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
    }
}
