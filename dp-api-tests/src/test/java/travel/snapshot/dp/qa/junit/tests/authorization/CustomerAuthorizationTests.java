package travel.snapshot.dp.qa.junit.tests.authorization;


import static org.apache.http.HttpStatus.SC_CREATED;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonAuthorizationTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class CustomerAuthorizationTests extends CommonAuthorizationTest {
    //    Load this test class specific test data
    private EntityNonNullMap<String, CustomerCreateDto> customerDtos = entitiesLoader.getCustomerDtos();

    @Test
    public void createAllCustomersUsingAuthorizationTest() {
        customerDtos.values().forEach(customer -> {
            customerHelpers.createCustomerWithAuthorization(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
    }
}
