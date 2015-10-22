package travel.snapshot.dp.qa.helpers;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

        return Arrays.stream(arrayString.split(",")).filter(s -> s!=null && !"".equals(s)).mapToInt(s -> Integer.parseInt(s)).boxed().toArray(size -> new Integer[size]);
    }
}
