package travel.snapshot.dp.qa.junit.loaders;

import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.YAML_DATA_PATH;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadEntities;

import lombok.Getter;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This singleton class loads all entities (customers, users, properties...) stored in yaml files and transforms them
 * into DTOs using YamlConstructor defined in YamlLoader class which is used by loadEntities method.
 *
 * Final objects storing the data (customerDtos, userDtos etc.) are Java Maps. Keys are simple strings used to differentiate
 * entities in the yaml files (customer1, customer2, ...) and values are corresponding DTOs.
 */
@Getter
public class EntitiesLoader {

    private static EntitiesLoader instance = null;

    private EntityNonNullMap<String, CustomerCreateDto> customerDtos;
    private EntityNonNullMap<String, UserCreateDto> userDtos;
    private EntityNonNullMap<String, UserCreateDto> snapshotUserDtos;

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
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/customers.yaml"));
        customerDtos = new EntityNonNullMap<>((LinkedHashMap<String, CustomerCreateDto>) yamlCustomers.get("customers"));

    }

    private void loadUsers() {
        Map<String, Object> yamlUsers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        userDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserCreateDto>) yamlUsers.get("users"));
    }

    private void loadSnapshotUsers() {
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        snapshotUserDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserCreateDto>) yamlCustomers.get("snapshotUsers"));
    }



}
