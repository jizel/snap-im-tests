package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class CustomersCRUDTests extends CommonTest {

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
