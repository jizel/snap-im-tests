package properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProps {

	public static String getPropValue(String arg) throws FileNotFoundException {
		String result = "";
		InputStream iStr;
		Properties prop = new Properties();
		String fileName = "config.properties";

		iStr = new FileInputStream(fileName);
		try {
			prop.load(iStr);
			result = prop.getProperty(arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
