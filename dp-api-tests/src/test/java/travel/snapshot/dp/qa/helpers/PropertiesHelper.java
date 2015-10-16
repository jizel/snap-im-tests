package travel.snapshot.dp.qa.helpers;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
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

}
