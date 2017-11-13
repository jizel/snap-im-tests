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
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupCreateDto;
import travel.snapshot.dp.qa.junit.utils.NonNullMapDecorator;

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

    private NonNullMapDecorator<String, CustomerCreateDto> customerDtos;
    private NonNullMapDecorator<String, UserCreateDto> userDtos;
    private NonNullMapDecorator<String, UserCreateDto> snapshotUserDtos;
    private NonNullMapDecorator<String, UserCreateDto> partnerUserDtos;
    private NonNullMapDecorator<String, PropertyCreateDto> propertyDtos;
    private NonNullMapDecorator<String, PropertySetCreateDto> propertySetDtos;
    private NonNullMapDecorator<String, RoleCreateDto> roleDtos;
    private NonNullMapDecorator<String, CustomerRoleCreateDto> customerRoleDtos;
    private NonNullMapDecorator<String, PropertyRoleCreateDto> propertyRoleDtos;
    private NonNullMapDecorator<String, PartnerCreateDto> partnerDtos;
    private NonNullMapDecorator<String, UserGroupCreateDto> userGroupDtos;
    private NonNullMapDecorator<String, ApplicationCreateDto> applicationDtos;
    private NonNullMapDecorator<String, ApplicationVersionCreateDto> applicationVersionDtos;
    private Map<String, Map<String, Object>> clients;

    private EntitiesLoader() {
        loadCustomers();
        loadUsers();
        loadSnapshotUsers();
        loadPartnerUsers();
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
        clients = (Map<String, Map<String, Object>>) yamlClients.get("clients");
    }

    private void loadCustomers() {
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/customers.yaml"));
        customerDtos = NonNullMapDecorator.of((Map<String, CustomerCreateDto>) yamlCustomers.get("customers"));

    }

    private void loadUsers() {
        Map<String, Object> yamlUsers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        userDtos = NonNullMapDecorator.of((Map<String, UserCreateDto>) yamlUsers.get("users"));
    }

    private void loadSnapshotUsers() {
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        snapshotUserDtos = NonNullMapDecorator.of((Map<String, UserCreateDto>) yamlCustomers.get("snapshotUsers"));
    }

    private void loadPartnerUsers() {
        Map<String, Object> yamlCustomers = loadEntities(String.format(YAML_DATA_PATH, "entities/users.yaml"));
        partnerUserDtos = NonNullMapDecorator.of((Map<String, UserCreateDto>) yamlCustomers.get("partnerUsers"));
    }

    private void loadProperties() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/properties.yaml"));
        propertyDtos = NonNullMapDecorator.of((Map<String, PropertyCreateDto>) yamlProperties.get("properties"));
    }

    private void loadPropertySets() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/property_sets.yaml"));
        propertySetDtos = NonNullMapDecorator.of((Map<String, PropertySetCreateDto>) yamlProperties.get("propertySets"));
    }

    private void loadRoles() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/roles.yaml"));
        roleDtos = NonNullMapDecorator.of((Map<String, RoleCreateDto>) yamlProperties.get("roles"));
        customerRoleDtos = NonNullMapDecorator.of((Map<String, CustomerRoleCreateDto>) yamlProperties.get("customerRoles"));
        propertyRoleDtos = NonNullMapDecorator.of((Map<String, PropertyRoleCreateDto>) yamlProperties.get("propertyRoles"));
    }

    private void loadPartners() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/partners.yaml"));
        partnerDtos = NonNullMapDecorator.of((Map<String, PartnerCreateDto>) yamlProperties.get("partners"));
    }

    private void loadUserGroups() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/user_groups.yaml"));
        userGroupDtos = NonNullMapDecorator.of((Map<String, UserGroupCreateDto>) yamlProperties.get("user_groups"));
    }

    private void loadApplications() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/applications.yaml"));
        applicationDtos = NonNullMapDecorator.of((Map<String, ApplicationCreateDto>) yamlProperties.get("applications"));
    }

    private void loadApplicationVersions() {
        Map<String, Object> yamlProperties = loadEntities(String.format(YAML_DATA_PATH, "entities/application_versions.yaml"));
        applicationVersionDtos = NonNullMapDecorator.of((Map<String, ApplicationVersionCreateDto>) yamlProperties.get("application_versions"));
    }
}
