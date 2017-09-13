package travel.snapshot.dp.qa.junit.loaders;

import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.YAML_DATA_PATH;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadEntities;

import lombok.Getter;
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
    private EntityNonNullMap<String, PropertyCreateDto> propertyDtos;
    private EntityNonNullMap<String, PropertySetCreateDto> propertySetDtos;
    private EntityNonNullMap<String, CustomerRoleCreateDto> customerRoleDtos;
    private EntityNonNullMap<String, PropertyRoleCreateDto> propertyRoleDtos;
    private EntityNonNullMap<String, PartnerCreateDto> partnerDtos;
    private EntityNonNullMap<String, UserGroupCreateDto> userGroupDtos;
    private EntityNonNullMap<String, ApplicationCreateDto> applicationDtos;
    private EntityNonNullMap<String, ApplicationVersionCreateDto> applicationVersionDtos;
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
        return new EntitiesLoader();
    }

    private void loadClients() {
        Map<String, Object> yamlClients = loadEntities(String.format(YAML_DATA_PATH, "entities/clients.yaml"));
        clients = (LinkedHashMap<String, Map<String, Object>>) yamlClients.get("clients");
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

    private void loadProperties() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/properties.yaml"));
        propertyDtos = new EntityNonNullMap<>((LinkedHashMap<String, PropertyCreateDto>) yamlProperties.get("properties"));
    }

    private void loadPropertySets() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/property_sets.yaml"));
        propertySetDtos = new EntityNonNullMap<>((LinkedHashMap<String, PropertySetCreateDto>) yamlProperties.get("propertySets"));
    }

    private void loadRoles() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/roles.yaml"));
        customerRoleDtos = new EntityNonNullMap<>((LinkedHashMap<String, CustomerRoleCreateDto>) yamlProperties.get("customerRoles"));
        propertyRoleDtos = new EntityNonNullMap<>((LinkedHashMap<String, PropertyRoleCreateDto>) yamlProperties.get("propertyRoles"));
    }

    private void loadPartners() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/partners.yaml"));
        partnerDtos = new EntityNonNullMap<>((LinkedHashMap<String, PartnerCreateDto>) yamlProperties.get("partners"));
    }

    private void loadUserGroups() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/user_groups.yaml"));
        userGroupDtos = new EntityNonNullMap<>((LinkedHashMap<String, UserGroupCreateDto>) yamlProperties.get("user_groups"));
    }

    private void loadApplications() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/applications.yaml"));
        applicationDtos = new EntityNonNullMap<>((LinkedHashMap<String, ApplicationCreateDto>) yamlProperties.get("applications"));
    }

    private void loadApplicationVersions() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/application_versions.yaml"));
        applicationVersionDtos = new EntityNonNullMap<>((LinkedHashMap<String, ApplicationVersionCreateDto>) yamlProperties.get("application_versions"));
    }
}
