package travel.snapshot.dp.qa.easyTests.loaders;

import lombok.extern.java.Log;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.fail;

import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;


@Log
public class YamlLoader extends BasicLoader {

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
//            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            fail("Invalid YAML input data. Exception: " + e1.getLocalizedMessage());
        }
        return data;
    }


    public static Map<String, List<Map<String, String>>> loadExamplesYaml(String filePath) {
        try {
            FileInputStream stream = new FileInputStream(filePath);
            Yaml yaml = new Yaml();
            return (Map<String, List<Map<String, String>>>) yaml.load(stream);
        } catch (FileNotFoundException e) {
//            log.severe(String.format(FILEDONTEXIST_MSG, filePath));
            return null;
        }
    }

    private static class YamlConstructor extends Constructor {
        public YamlConstructor() {
            this.yamlConstructors.put(new Tag("!localDate"), new LocalDateConstructor());

            addTypeDescription(new TypeDescription(ApplicationDto.class, "!application"));
            addTypeDescription(new TypeDescription(ApplicationVersionDto.class, "!applicationVersion"));
            addTypeDescription(new TypeDescription(UserCreateDto.class, "!user"));
            addTypeDescription(new TypeDescription(CustomerRoleDto.class, "!customerRole"));
        }

        private static class LocalDateConstructor extends AbstractConstruct {

            public Object construct(Node node) {
                return LocalDate.now();
            }
        }
    }
}
