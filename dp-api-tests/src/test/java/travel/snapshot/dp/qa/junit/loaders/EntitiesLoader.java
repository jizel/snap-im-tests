package travel.snapshot.dp.qa.junit.loaders;

import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.YAML_DATA_PATH;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadEntities;

import lombok.Getter;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
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

    private EntityNonNullMap<String, CustomerDto> customerDtos;
    private EntityNonNullMap<String, UserCreateDto> userDtos;
    private EntityNonNullMap<String, UserCreateDto> snapshotUserDtos;
    private EntityNonNullMap<String, PropertyDto> propertyDtos;
    private EntityNonNullMap<String, PropertySetDto> propertySetDtos;
    private EntityNonNullMap<String, CustomerRoleDto> customerRoleDtos;
    private EntityNonNullMap<String, PropertyRoleDto> propertyRoleDtos;
    private EntityNonNullMap<String, PartnerDto> partnerDtos;
    private EntityNonNullMap<String, UserGroupDto> userGroupDtos;
    private EntityNonNullMap<String, ApplicationDto> applicationDtos;
    private EntityNonNullMap<String, ApplicationVersionDto> applicationVersionDtos;
    private LinkedHashMap<String, Map<String, Object>> clients;

    private EntitiesLoader() {
        loadCustomers();
        loadUsers();
        loadSnapshotUsers();
        loadProperties();
        loadPropertySets();
        loadRoles();
        loadPartners();
        loadUserGroups();
        loadApplications();
        loadApplicationVersions();
        loadClients();
    }

    public static EntitiesLoader getInstance() {
//        if(instance == null) {
//            instance = new EntitiesLoader();
//        }
//        return instance;
        return new EntitiesLoader();
    }

    private void loadClients() {
        Map<String, Object> yamlClients = loadEntities(String.format(YAML_DATA_PATH, "entities/clients.yaml"));
        clients = (LinkedHashMap<String, Map<String, Object>>) yamlClients.get("clients");
    }

    private void loadCustomers() {
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/customers.yaml"));
        customerDtos = new EntityNonNullMap<>((LinkedHashMap<String, CustomerDto>) yamlCustomers.get("customers"));

    }

    private void loadUsers() {
        Map<String, Object> yamlUsers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        userDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserCreateDto>) yamlUsers.get("users"));
    }

    private void loadSnapshotUsers() {
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        snapshotUserDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserCreateDto>) yamlCustomers.get("snapshotUsers"));
    }

    private void loadProperties() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/properties.yaml"));
        propertyDtos = new EntityNonNullMap<>((LinkedHashMap<String, PropertyDto>) yamlProperties.get("properties"));
    }

    private void loadPropertySets() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/property_sets.yaml"));
        propertySetDtos = new EntityNonNullMap<>((LinkedHashMap<String, PropertySetDto>) yamlProperties.get("propertySets"));
    }

    private void loadRoles() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/roles.yaml"));
        customerRoleDtos = new EntityNonNullMap<>((LinkedHashMap<String, CustomerRoleDto>) yamlProperties.get("customerRoles"));
        propertyRoleDtos = new EntityNonNullMap<>((LinkedHashMap<String, PropertyRoleDto>) yamlProperties.get("propertyRoles"));
    }

    private void loadPartners() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/partners.yaml"));
        partnerDtos = new EntityNonNullMap<>((LinkedHashMap<String, PartnerDto>) yamlProperties.get("partners"));
    }

    private void loadUserGroups() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/user_groups.yaml"));
        userGroupDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserGroupDto>) yamlProperties.get("user_groups"));
    }

    private void loadApplications() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/applications.yaml"));
        applicationDtos = new EntityNonNullMap<>((LinkedHashMap<String, ApplicationDto>) yamlProperties.get("applications"));
    }

    private void loadApplicationVersions() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/application_versions.yaml"));
        applicationVersionDtos = new EntityNonNullMap<>((LinkedHashMap<String, ApplicationVersionDto>) yamlProperties.get("application_versions"));
    }
}
