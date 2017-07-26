package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class CustomersCRUDTests extends CommonTest {

    //    Load this test class specific test data
    private static EntityNonNullMap<String, CustomerCreateDto> customerDtos = entitiesLoader.getCustomerDtos();

    @Test
    public void createAllCustomersTest() {
        customerDtos.values().forEach(customer -> {
            customerHelpers.followingCustomerIsCreated(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has
    }
}
