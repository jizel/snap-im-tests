package travel.snapshot.dp.qa;

import java.io.IOException;
import java.util.Properties;

/**
 * Reads configuration properties.
 */
public class ConfigProps {

    private static final String CONFIG_FILE = "config.properties";

    /**
     * Reads property value from java system properties or configuration file (config.properties).
     * Java system properties have a precedence before configuration files.
     *
     * @param propName name of the property - key
     * @return property value or
     */
    public static String getPropValue(String propName) {
        if (System.getProperty(propName) != null) {
            return System.getProperty(propName);
        }

        try {
            final Properties props = new Properties();
            props.load(ConfigProps.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
            return props.getProperty(propName);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read configuration from resource: "
                    + CONFIG_FILE);
        }
    }
}
