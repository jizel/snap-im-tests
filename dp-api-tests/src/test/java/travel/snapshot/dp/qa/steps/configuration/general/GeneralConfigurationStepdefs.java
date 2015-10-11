package travel.snapshot.dp.qa.steps.configuration.general;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.model.Configuration;
import travel.snapshot.dp.qa.model.ConfigurationType;
import travel.snapshot.dp.qa.serenity.configuration.ConfigurationSteps;

import java.util.List;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class GeneralConfigurationStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private ConfigurationSteps configurationSteps;


    @Given("^The following configuration types exist$")
    public void The_following_configuration_types_exist(List<ConfigurationType> configurationTypes) throws Throwable {
        configurationSteps.followingConfigurationTypesExist(configurationTypes, 0);
    }

    @When("^Configuration type is created$")
    public void Configuration_type_is_created(List<ConfigurationType> configurationTypes) throws Throwable {
        configurationSteps.followingConfigurationTypeIsCreated(configurationTypes.get(0));
    }

    @When("^Data \'(.*)\' is used for \"([^\"]*)\"$")
    public void Data_is_used_for(String jsonData, String method) throws Throwable {
        switch (method) {
            case "POST": {
                configurationSteps.dataIsUsedForCreation(jsonData);
                break;
            }
            default:
                break;
        }
    }

    @When("^Configuration type with identifier \"([^\"]*)\" is deleted$")
    public void Configuration_type_with_identifier_is_deleted(String identifier) throws Throwable {
        configurationSteps.tryDeleteConfigurationType(identifier);
    }

    @Then("^Configuration type with identifier \"([^\"]*)\" doesn't exist$")
    public void Configuration_type_with_identifier_doesn_t_exist(String identifier) throws Throwable {
        configurationSteps.configurationTypeDoesntExist(identifier);
    }

    @When("^Configuration type with with identifier \"([^\"]*)\"  is got$")
    public void Configuration_type_with_with_identifier_is_got(String identifier) throws Throwable {
        configurationSteps.getConfigurationTypeWithId(identifier);
    }


    @When("^List of configuration types is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter empty and sort empty$")
    public void List_of_configuration_types_is_got_with_limit_and_cursor_and_filter_empty_and_sort_empty(String limit, String cursor) throws Throwable {
        configurationSteps.listOfConfigurationTypesisGot(limit, cursor);
    }

    @Then("^There are (\\d+) configuration types returned$")
    public void There_are_configuration_types_returned(int count) throws Throwable {
        configurationSteps.numberOfConfigurationTypesIsInResponse(count);
    }

    @Then("^Body contains configuration type with identifier \"([^\"]*)\" and description \"([^\"]*)\"$")
    public void Body_contains_configuration_type_with_identifier_and_description(String identifier, String description) throws Throwable {
        configurationSteps.bodyContainsConfigurationType(identifier, description);
    }

    @Then("^\"([^\"]*)\" header is set and contains configuration type with identifier \"([^\"]*)\"$")
    public void header_is_set_and_contains_configuration_type_with_identifier(String header, String identifier) throws Throwable {
        configurationSteps.compareHeaderWithIdentifier(header, identifier);
    }

    @Given("^The following configuration types exist with (\\d+) random text items$")
    public void The_following_configuration_types_exist_with_random_text_items(int count, List<ConfigurationType> configurationTypes) throws Throwable {
        configurationSteps.followingConfigurationTypesExist(configurationTypes, count);
    }

    @When("^Configuration is created for configuration type \"([^\"]*)\"$")
    public void Configuration_is_created_for_configuration_type(String identifier, List<Configuration> configurations) throws Throwable {
        configurationSteps.followingConfigurationIsCreated(configurations.get(0), identifier);
    }

    @Then("^Body contains configuration with key \"([^\"]*)\" and value \"([^\"]*)\"$")
    public void Body_contains_configuration_with_key_and_value(String key, String value) throws Throwable {
        configurationSteps.bodyContainsConfiguration(key, value);
    }


    @Then("^\"([^\"]*)\" header is set and contains configuration with key \"([^\"]*)\"$")
    public void header_is_set_and_contains_configuration_with_key(String header, String key) throws Throwable {
        configurationSteps.compareHeaderWithKey(header, key);
    }

    @Given("^The following configurations exist for configuration type identifier \"([^\"]*)\"$")
    public void The_following_configuration_exist_for_configration_type_identifier(String identifier, List<Configuration> configurations) throws Throwable {
        configurationSteps.followingConfigurationsExist(configurations, identifier);
    }

    @Then("^Configuration with key \"([^\"]*)\" doesn't exist for configuration type \"([^\"]*)\"$")
    public void Configuration_with_key_doesn_t_exist_for_configuration_type(String key, String identifier) throws Throwable {
        configurationSteps.configurationDoesntExistForIdentifier(key, identifier);
    }

    @When("^Configuration with key \"([^\"]*)\"  is got from configuration type \"([^\"]*)\"$")
    public void Configuration_with_key_is_got_from_configuration_type(String key, String identifier) throws Throwable {
        configurationSteps.getConfigurationWithKeyForIdentifier(key, identifier);
    }

    @When("^List of configurations is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter empty and sort empty for configuration type \"([^\"]*)\"$")
    public void List_of_configurations_is_got_with_limit_and_cursor_and_filter_empty_and_sort_empty_for_configuration_type(String limit, String cursor, String identifier) throws Throwable {
        configurationSteps.listOfConfigurationsIsGot(limit, cursor, identifier);
    }

    @Then("^Returned configuration value is \"([^\"]*)\"$")
    public void Returned_configuration_value_is(String value) throws Throwable {
        configurationSteps.valueInResponseIs(value);
    }

    @Then("^There are (\\d+) configurations returned$")
    public void There_are_configurations_returned(int count) throws Throwable {
        configurationSteps.numberOfConfigurationsIsInResponse(count);
    }


    @When("^Configuration with identifier \"([^\"]*)\" is deleted from identifier \"([^\"]*)\"$")
    public void Configuration_with_identifier_is_deleted_from_identifier(String key, String identifier) throws Throwable {
        configurationSteps.tryDeleteConfiguration(key, identifier);
    }
}