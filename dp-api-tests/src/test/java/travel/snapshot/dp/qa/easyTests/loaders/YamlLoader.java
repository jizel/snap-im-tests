package travel.snapshot.dp.qa.easyTests.loaders;

import static org.junit.Assert.fail;

import lombok.extern.java.Log;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.loader.Loader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by zelezny on 5/15/2017.
 */
@Log
public class YamlLoader implements Loader {

    /**
     * Construct a new YamlLoader
     */
    public YamlLoader() {
    }

    /**
     * Load YAML data and return as Java object for another processing.
     *
     * Using SnakeYaml library to load the data.
     */
    @Override
    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        Yaml yaml = new Yaml();
        Map<String, List<Map<String, Object>>> data = null;
        try {
            // Tests expect data to be in certain format. See sampleTestsData.yaml for how it should look like.
            data = (Map<String, List<Map<String, Object>>>) yaml.load(resource.getInputStream());

        } catch (IOException e) {
            log.severe("IOException occurred while trying to Load the resource " + resource.getResourceName() + ". Moving to the next resource. Exception: " + e.getLocalizedMessage());
            fail("Invalid YAML input data. Exception: " + e.getLocalizedMessage());
        }
        return data;
    }

    @Override
    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        throw new UnsupportedOperationException("Write data operation is not supported by YamlLoader.");
    }
}
