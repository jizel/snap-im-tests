package travel.snapshot.dp.qa.serenity.customers;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class CustomerSteps extends BasicSteps {

    public static final String SESSION_CUSTOMERS = "customers";
    private static final String SESSION_CREATED_CUSTOMER = "created_customer";
    private static final String SESSION_CUSTOMER_ID = "customer_id";

    public CustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    @Step
    public void followingCustomersExist(List<Customer> customers) {
        customers.forEach(t -> {
            t.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
            Customer existingCustomer = getCustomerByCode(t.getCode());
            if (existingCustomer != null) {
                deleteCustomer(existingCustomer.getCustomerId());
            }
            createCustomer(t);
        });
        Serenity.setSessionVariable(SESSION_CUSTOMERS).to(customers);
    }

    @Step
    public void fileIsUsedForCreation(String filename) {
        Customer customer = getCustomerFromFile(this.getClass().getResourceAsStream(filename));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createCustomer(customer);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }


    @Step
    public void followingCustomerIsCreated(Customer customer) {
        customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Customer existingCustomer = getCustomerByCode(customer.getCode());
        if (existingCustomer != null) {
            deleteCustomer(existingCustomer.getCustomerId());
        }
        Response response = createCustomer(customer);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void compareCustomerOnHeaderWithStored(String headerName) {
        Customer originalCustomer = Serenity.sessionVariableCalled(SESSION_CREATED_CUSTOMER);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName);
        spec.get(customerLocation).then()
                .body("salesforce_id", is(originalCustomer.getSalesforceId()))
                .body("company_name", is(originalCustomer.getCompanyName()))
                .body("commercial_subscription_id", is(originalCustomer.getCommercialSubscriptionId()))
                .body("code", is(originalCustomer.getCode()))
                .body("email", is(originalCustomer.getEmail()))
                .body("vat_id", is(originalCustomer.getVatId()));

    }

    private Customer getCustomerFromFile(InputStream inputStream) {
        Customer customer = from(inputStream).getObject("", Customer.class);
        return customer;
    }

    private Response createCustomer(Customer t) {
        return given().spec(spec).basePath("/identity/customers")
                .body(t)
                .when().post();

    }

    private Response updateCustomer(String id, Map<String, Object> customer) {
        return given().spec(spec).basePath("/identity/customers")
                .body(customer)
                .when().post("/{id}", id);

    }

    private Response deleteCustomer(String id) {
        return given().spec(spec).basePath("/identity/customers")
                .when().delete("/{id}", id);
    }

    private Response getCustomer(String id) {
        return given().spec(spec).basePath("/identity/customers")
                .when().get("/{id}", id);
    }

    private Response activateCustomer(String id) {
        return given().spec(spec).basePath("/identity/customers")
                .when().post("/{id}/active", id);
    }

    private Response inactivateCustomer(String id) {
        return given().spec(spec).basePath("/identity/customers")
                .when().post("/{id}/inactive", id);
    }

    /**
     * getting customers over rest api, if limit and cursor is null or empty, it's not added to query string
     *
     * @param limit
     * @param cursor
     * @return
     */
    private Response getCustomers(String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath("/identity/customers");

        if (cursor != null && !"".equals(cursor)) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null && !"".equals(limit)) {
            requestSpecification.parameter("limit", limit);
        }
        return requestSpecification.when().get();
    }

    private Customer getCustomerByCode(String code) {
        Customer[] customers = getCustomers("100", "0").as(Customer[].class);
        return Arrays.asList(customers).stream().filter(p -> code.equals(p.getCode())).findFirst().orElse(null);
    }


    @Step
    public void getCustomerWithId(String customerId) {
        Response resp = getCustomer(customerId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void getCustomerWithCode(String code) {
        //TODO implement actual customer search
        Customer customerFromList = getCustomerByCode(code);

        Response resp = getCustomer(customerFromList.getCustomerId());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void deleteCustomerWithId(String customerId) {
        Response resp = deleteCustomer(customerId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void deleteCustomerWithCode(String code) {
        String customerId = getCustomerByCode(code).getCustomerId();
        Response resp = deleteCustomer(customerId);//delete customer
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
        Serenity.setSessionVariable(SESSION_CUSTOMER_ID).to(customerId);//store to session
    }

    @Step
    public void customerIdInSessionDoesntExist() {
        String customerId = Serenity.sessionVariableCalled(SESSION_CUSTOMER_ID);

        Response response = getCustomer(customerId);
        response.then().statusCode(404);
    }

    @Step
    public void listOfCustomersIsGotWith(String limit, String cursor) {
        Response response = getCustomers(limit, cursor);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void numberOfCustomersIsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Customer[] customers = response.as(Customer[].class);
        assertEquals("There should be " + count + " customers got", count, customers.length);
    }

    @Step
    public void updateCustomerWithCode(String code, Customer updatedCustomer) {
        Customer original = getCustomerByCode(code);

        Map<String, Object> customer = new HashMap<>();
        if (updatedCustomer.getEmail() != null && !"".equals(updatedCustomer.getEmail())) {
            customer.put("email", updatedCustomer.getEmail());
        }
        if (updatedCustomer.getCommercialSubscriptionId() != null && !"".equals(updatedCustomer.getCommercialSubscriptionId())) {
            customer.put("commercial_subscription_id", updatedCustomer.getCommercialSubscriptionId());
        }
        if (updatedCustomer.getCompanyName() != null && !"".equals(updatedCustomer.getCompanyName())) {
            customer.put("company_name", updatedCustomer.getCompanyName());
        }
        if (updatedCustomer.getVatId() != null && !"".equals(updatedCustomer.getVatId())) {
            customer.put("vat_id", updatedCustomer.getVatId());
        }
        if (updatedCustomer.getPhone()!=null && !"".equals(updatedCustomer.getPhone())) {
            customer.put("phone", updatedCustomer.getPhone());
        }

        Response response = updateCustomer(original.getCustomerId(), customer);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void activateCustomerWithCode(String code) {
        Customer customer = getCustomerByCode(code);
        Response response = activateCustomer(customer.getCustomerId());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void isActiveSetTo(boolean activeFlag, String code) {
        Customer customer = getCustomerByCode(code);
        fail("How to check it was changed?");
    }

    @Step
    public void inactivateCustomerWithCode(String code) {
        Customer customer = getCustomerByCode(code);
        Response response = inactivateCustomer(customer.getCustomerId());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void bodyContainsCustomerWith(String atributeName, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, is(value));
    }
}
