package travel.snapshot.dp.qa.steps.identity.properties;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class PropertiesStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private PropertySteps propertySteps;
    @Steps
    private UsersSteps usersSteps;

    // --- given ---

    @Given("^The following properties exist with random address and billing address$")
    public void The_following_properties_exist(List<PropertyDto> properties) throws Throwable {
        propertySteps.followingPropertiesExist(properties);
    }

    @Given("^All users are removed for properties with codes: (.*)$")
    public void All_users_are_removed_for_properties_with_codes(List<String> propertyCodes) throws Throwable {
        propertySteps.removeAllUsersFromPropertiesWithCodes(propertyCodes);
    }

    @Given("^Relation between user with username \"([^\"]*)\" and property with code \"([^\"]*)\" exists$")
    public void Relation_between_user_with_username_and_property_with_code_exists(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.relationExistsBetweenUserAndProperty(u, propertyCode);
    }

    // --- when ---

    @When("^Property with code \"([^\"]*)\" exists$")
    public void Property_with_code_exists(String code) throws Throwable {
        propertySteps.getPropertyByCode(code);
    }

    @When("^Property with code \"([^\"]*)\" exists with etag$")
    public void Property_with_code_exists_with_etag(String code) throws Throwable {
        propertySteps.getPropertyByCodeUsingEtag(code);
    }

    @When("^Property with code \"([^\"]*)\" exists for etag, forced new etag through update$")
    public void Property_with_code_exists_for_etag_forced_new_etag_through_update(String code) throws Throwable {
        propertySteps.getPropertyWithCodeUsingEtagAfterUpdate(code);
    }

    @When("^Nonexistent property id sent$")
    public void Nonexistent_property_id_sent() throws Throwable {
        propertySteps.getPropertyByID("nonexistent_id");
    }

    @When("^List of properties is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_properties_exists_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                  @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                  @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                  @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                  @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfPropertiesExistsWith(limit, cursor, filter, sort, sortDesc);
    }

    @When("^Property is created with random address and billing address$")
    public void property_is_created(List<PropertyDto> properties) throws Throwable {
        propertySteps.followingPropertyIsCreated(properties.get(0));
    }

    @When("^A property from country \"([^\"]*)\" region \"([^\"]*)\" code \"([^\"]*)\" email \"([^\"]*)\" is created$")
    public void property_from_country_region_code_email_is_created(String country, String region, String code, String email) {
        AddressDto address = new AddressDto();
        PropertyDto property = new PropertyDto();
        address.setAddressLine1("someAddress");
        address.setCity("someCity");
        address.setZipCode("1234");
        address.setCountry(country);
        address.setRegion(region);
        property.setPropertyName("someProperty");
        property.setPropertyCode(code);
        property.setEmail(email);
        property.setIsDemoProperty(true);
        property.setTimezone("GMT");
        propertySteps.followingPropertyIsCreatedWithAddress(property, address);
    }

    @When("^Property with code \"([^\"]*)\" is deleted$")
    public void Property_with_code_is_deleted(String code) throws Throwable {
        propertySteps.deletePropertyWithCode(code);
    }

    @When("^Nonexistent property id is deleted$")
    public void Nonexistent_property_id_is_deleted() throws Throwable {
        propertySteps.deletePropertyById("nonexistent_id");
    }

    @When("^User with username \"([^\"]*)\" is added to property with code \"([^\"]*)\"$")
    public void User_with_username_is_added_to_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.userIsAddedToProperty(u, propertyCode);
    }

    @When("^User with username \"([^\"]*)\" is removed from property with code \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.userIsDeletedFromProperty(u, propertyCode);
    }

    @When("^Nonexistent user is removed from property with code \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_property_with_code(String propertyCode) throws Throwable {
        UserDto user = new UserDto();
        user.setUserId("nonexistent");
        propertySteps.userIsDeletedFromProperty(user, propertyCode);
    }

    @When("^List of users for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfUsersIsGotWith(propertyCode, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of customers for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfCustomersIsGotWith(propertyCode, limit, cursor, filter, sort, sortDesc);
    }

    // --- then ---

    @Then("^Body contains property with attribute \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyContainsEntityWith(atributeName);
    }

    @Then("^Body contains property with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute_value(String atributeName, String value) throws Throwable {
        propertySteps.bodyContainsPropertyWith(atributeName, value);
    }

    @Then("^Body does not contain property with attribute \"([^\"]*)\"$")
    public void Body_does_not_contain_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyDoesntContainEntityWith(atributeName);
    }

    @Then("^There are (\\d+) properties returned$")
    public void There_are_properties_returned(int count) throws Throwable {
        propertySteps.numberOfEntitiesInResponse(PropertyDto.class, count);
    }

    @Then("^All customers are customers of property with code \"([^\"]*)\"$")
    public void each_customer_is_a_customer_of_property_with_code(String propertyCode) throws Throwable {
        propertySteps.allCustomersAreCustomersOfProperty(propertyCode);
    }

    // --- and ---

    @Then("^Property with same id doesn't exist$")
    public void Property_with_same_id_doesn_t_exist() throws Throwable {
        propertySteps.propertyIdInSessionDoesntExist();
    }

    @Then("^User with username \"([^\"]*)\" isn't there for property with code \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.userDoesntExistForProperty(u, propertyCode);
    }

    @Then("^There are property users with following usernames returned in order: (.*)$")
    public void There_are_property_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        propertySteps.usernamesAreInResponseInOrder(usernames);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same property$")
    public void header_is_set_and_contains_the_same_property(String header) throws Throwable {
        propertySteps.comparePropertyOnHeaderWithStored(header);
    }

    @When("^Property with code \"([^\"]*)\" is activated$")
    public void property_With_Code_Is_Activated(String code) throws Throwable {
        propertySteps.activatePropertyWithCode(code);
    }

    @When("^Property with code \"([^\"]*)\" is inactivated$")
    public void property_With_Code_Is_Inactivated(String code) throws Throwable {
        propertySteps.inactivatePropertyWithCode(code);
    }

    @When("^Property with non existing property id \"([^\"]*)\" is inactivated$")
    public void property_With_Non_Existing_Property_Id_Is_Inactivated(String id) throws Throwable {
        propertySteps.inactivateNotExistingProperty(id);
    }

    @When("^Property with non existing property id \"([^\"]*)\" is activated$")
    public void property_With_Non_Existing_Property_Id_Is_Activated(String id) throws Throwable {
        propertySteps.activateNotExistingProperty(id);
    }

    @Then("^Property with code \"([^\"]*)\" is active$")
    public void Customer_with_code_is_active(String code) throws Throwable {
        propertySteps.isActiveSetTo(true, code);
    }

    @Then("^Property with code \"([^\"]*)\" is not active$")
    public void Customer_with_code_is_not_active(String code) throws Throwable {
        propertySteps.isActiveSetTo(false, code);
    }

    @Then("^Property \"([^\"]*)\" is not assigned to customer \"([^\"]*)\"$")
    public void propertyIsNotAssignedToCustomer(String propertyCode, String customerCode) throws Throwable {
        propertySteps.customerDoesNotExistForProperty(customerCode, propertyCode);
    }

    @When("^Property set with name \"([^\"]*)\" for property with code \"([^\"]*)\" is got$")
    public void Property_set_with_name_for_property_with_code_is_got(String propertySetName, String propertyCode) {
        propertySteps.propertyPropertySetWithNameIsGot(propertySetName, propertyCode);
    }

    @When("^List of property sets is got for property with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_is_got_for_property_with_id_and_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyId,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String sortDesc) {
        propertySteps.listOfPropertiesPropertySetsIsGot(propertyId, limit, cursor, filter, sort, sortDesc);
    }

    // TODO reuse existing code

//
//    @When("^File \"([^\"]*)\" is used for \"([^\"]*)\"$")
//    public void customer_file_is_used_for_method(String fileName, String method) throws Throwable {
//        switch (method) {
//            case "POST": {
//                propertySteps.fileIsUsedForCreation("/messages/identity/customers/" + fileName);
//                break;
//            }
//            default: break;
//        }
//
//    }
//
//    @When("^Customer with code \"([^\"]*)\" is updated with data$")
//    public void Customer_with_code_is_updated_with_data(String code, List<Customer> customers) throws Throwable {
//        propertySteps.updateCustomerWithCode(code, customers.get(0));
//    }
//
//    @Then("^Updated customer with code \"([^\"]*)\" has data$")
//    public void Updated_customer_with_code_has_data(String code, List<Customer> customers) throws Throwable {
//        propertySteps.customerWithCodeHasData(code, customers.get(0));
//    }
//
//    @When("^Customer with code \"([^\"]*)\" is updated with data if updated before$")
//    public void Customer_with_code_is_updated_with_data_if_updated_before(String code, List<Customer> customers) throws Throwable {
//        propertySteps.updateCustomerWithCodeIfUpdatedBefore(code, customers.get(0));
//    }

}
