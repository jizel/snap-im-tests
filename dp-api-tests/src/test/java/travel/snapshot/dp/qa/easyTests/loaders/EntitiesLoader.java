package travel.snapshot.dp.qa.easyTests.loaders;

import static travel.snapshot.dp.qa.easyTests.loaders.YamlLoader.YAML_DATA_PATH;
import static travel.snapshot.dp.qa.easyTests.loaders.YamlLoader.load;

import lombok.Getter;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.qa.easyTests.utils.EntityNonNullMap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This singleton class loads all entities (customers, users, properties...) stored in yaml files and transforms them
 * into DTOs using YamlConstructor defined in YamlLoader class which is used by load method.
 *
 * Final objects storing the data (customerDtos, userDtos etc.) are Java Maps. Keys are simple strings used to differentiate
 * entities in the yaml files (customer1, customer2, ...) and values are corresponding DTOs.
 */
public class EntitiesLoader {

    private static EntitiesLoader instance = null;

    @Getter private EntityNonNullMap<String, CustomerCreateDto> customerDtos;
    @Getter private EntityNonNullMap<String, UserCreateDto> userDtos;
    @Getter private EntityNonNullMap<String, UserCreateDto> snapshotUserDtos;

    private EntitiesLoader() {
        loadCustomers();
        loadUsers();
        loadSnapshotUsers();
    }

    public static EntitiesLoader getInstance() {
        if(instance == null) {
            instance = new EntitiesLoader();
        }
        return instance;
    }

    private void loadCustomers() {
        Map<String, Object> yamlCustomers = load(String.format(YAML_DATA_PATH, "entities/customers.yaml"));
        customerDtos = new EntityNonNullMap<>((LinkedHashMap<String, CustomerCreateDto>) yamlCustomers.get("customers"));

    }

    private void loadUsers() {
        Map<String, Object> yamlUsers = load(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        userDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserCreateDto>) yamlUsers.get("users"));
    }

    private void loadSnapshotUsers() {
        Map<String, Object> yamlCustomers = load(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        snapshotUserDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserCreateDto>) yamlCustomers.get("snapshotUsers"));
    }



}
