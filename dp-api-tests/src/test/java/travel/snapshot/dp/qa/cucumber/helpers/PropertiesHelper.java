package travel.snapshot.dp.qa.cucumber.helpers;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by sedlacek on 9/30/2015.
 */
public class PropertiesHelper {
    public Properties properties = null;

    public PropertiesHelper() {
        String apiTestsConfigProperty = System.getProperty("api-tests-config");

        properties = new Properties();
        InputStream stream = null;
        if (StringUtils.isNotEmpty(apiTestsConfigProperty)) {
            try {
                stream = new FileInputStream(apiTestsConfigProperty);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //If file cannot be loaded, take default one
        if (stream == null) {
            stream = PropertiesHelper.class
                    .getResourceAsStream("/dp.properties");
        }

        try {
            properties.load(stream);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public Integer[] getListOfInt(String key) {
        String arrayString = getProperty(key);

        return Arrays.stream(arrayString.split(",")).filter(s -> s != null && !"".equals(s)).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
    }
}
