package travel.snapshot.dp.qa.junit.loaders;

import static org.junit.Assert.*;

import lombok.extern.java.Log;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import travel.snapshot.dp.api.identity.model.ApplicationCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleCreateDto;
import travel.snapshot.dp.api.identity.model.PartnerCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetCreateDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupCreateDto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Log
public class YamlLoader {
    private static final String FILEDONTEXIST_MSG = "Provided file does not exist: %s";
    public static final String YAML_DATA_PATH = "src/test/resources/yaml/%s";
    private static final String FAIL_MESSAGE = "Invalid YAML input data. Exception: %s";

    /**
     * Construct a new YamlLoader
     */
    public YamlLoader() {
    }

    public static Map<String, Object> loadData(String filePath) {
        Yaml yaml = new Yaml(new YamlConstructor());
        Map<String, Object> data = null;
        try {
            FileInputStream stream = new FileInputStream(filePath);
            data = (Map<String, Object>) yaml.load(stream);
        } catch (FileNotFoundException e1) {
            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            fail(String.format(FAIL_MESSAGE, e1.getLocalizedMessage()));
        }
        return data;
    }

    public static Map<String, List<Map<String, String>>> loadExamplesYaml(String filePath) {
        try {
            FileInputStream stream = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            return (Map<String, List<Map<String, String>>>) yaml.load(stream);
        } catch (FileNotFoundException e) {
            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            return null;
        }
    }

    public static Map<String, Map<String, List<String>>> loadYamlTables(String filePath) {
        try {
            FileInputStream stream = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            return (Map<String, Map<String, List<String>>>) yaml.load(stream);
        } catch (FileNotFoundException e) {
            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            return null;
        }
    }

    public static Map<String, Object> loadEntities(String filePath) {
        Yaml yaml = new Yaml(new YamlConstructor());
        Map<String, Object> data = null;
        try {
            FileInputStream stream = new FileInputStream(filePath);
            data = (Map<String, Object>) yaml.load(stream);
        } catch (FileNotFoundException e1) {
            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            fail(String.format(FAIL_MESSAGE, e1.getLocalizedMessage()));
        }
        return data;
    }

    public static Map<String, Map<String, Object>> loadTestData(String filePath) {
        try {
            FileInputStream stream = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            return (Map<String, Map<String, Object>>) yaml.load(stream);
        } catch (FileNotFoundException e) {
            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            return null;
        }
    }


    private static class YamlConstructor extends Constructor {
        public YamlConstructor() {
            this.yamlConstructors.put(new Tag("!localDate"), new LocalDateConstructor());

            addTypeDescription(new TypeDescription(CustomerCreateDto.class, "!customer"));
            addTypeDescription(new TypeDescription(ApplicationCreateDto.class, "!application"));
            addTypeDescription(new TypeDescription(ApplicationVersionCreateDto.class, "!applicationVersion"));
            addTypeDescription(new TypeDescription(UserCreateDto.class, "!user"));
            addTypeDescription(new TypeDescription(PropertyCreateDto.class, "!property"));
            addTypeDescription(new TypeDescription(PropertySetCreateDto.class, "!propertySet"));
            addTypeDescription(new TypeDescription(CustomerRoleCreateDto.class, "!customerRole"));
            addTypeDescription(new TypeDescription(PropertyRoleCreateDto.class, "!propertyRole"));
            addTypeDescription(new TypeDescription(PartnerCreateDto.class, "!partner"));
            addTypeDescription(new TypeDescription(UserGroupCreateDto.class, "!user_group"));
            addTypeDescription(new TypeDescription(ApplicationCreateDto.class, "!application"));
        }
    }

    private static class LocalDateConstructor extends AbstractConstruct {

        public Object construct(Node node) {
            return LocalDate.now();
        }
    }


    public static List<Map<String, String>> selectExamplesForTest(Map<String, List<Map<String, String>>> testData, String testName) {
        if (testData.get(testName) == null) {
            fail("No test data for test named " + testName);
        }
        return testData.get(testName);
    }

    public static Map<String, Object> getSingleTestData(Map<String, Map<String, Object>> testData, String testName) {
        if (testData.get(testName) == null) {
            fail("No test data for test named " + testName);
        }
        return testData.get(testName);
    }

    public static List<Map<String, String>> selectExamplesForTestFromTable(Map<String, Map<String, List<String>>> dataSet, String testName) {
        Map<String, List<String>> testData = dataSet.get(testName);
        List<Map<String, String>> outputTestData = new LinkedList<>();

//        Get size of the list containing scenario data and check it is the same for all the entries
        int listSize = testData.entrySet().iterator().next().getValue().size();
        testData.values().iterator().forEachRemaining(list -> assertEquals("Number of values must be the same for all attributes!", list.size(), listSize));

        for (int i = 0; i < listSize; i++) {
            Map<String, String> valueMap = new LinkedHashMap<>();
            for (String key : testData.keySet()) {
                valueMap.put(key, testData.get(key).get(i));
            }
            outputTestData.add(valueMap);
        }

        return outputTestData;

    }
}
