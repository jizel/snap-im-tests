package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static junit.framework.TestCase.assertTrue;
import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Tests for Customer code feature
 * When creating a customer, an unique customer code is generated according to following rules:
 * - Customer code is always returned (and unique)
 * - Error is returned when trying to create/update customer_code field manually
 * - Customer code is generated from his name, country and city according to rules specified in DP-1222
 * - Customer code always contains only CAPITAL english (latin) letters. Even when customer's name contains chinese, arabic or any other characters.
 */
@RunWith(JUnitParamsRunner.class)
public class CustomerCodeTests extends CommonTest{
    private static final String EXAMPLES = "src/test/resources/csv/customers/";

    @Test
    @FileParameters(EXAMPLES + "generatedCustomerCodeTestExamples.csv")
    public void generatedCustomerCodeTests(String name) throws Exception {
        testCustomer1.setName(name);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("customer_code");
        String generatedCode = getAttributeValue("customer_code");
        assertTrue("Attribute " + generatedCode + " is not sequence of capital latin letters and digits. It is: "+ generatedCode,
                generatedCode.matches("[A-Z0-9]+"));
    }

    @Test
    public void customerCodeCannotBeCreatedManually() throws Exception {
        CustomerDto customerWithCode = new CustomerDto();
        customerWithCode.setName("customerWithManualCode");
        customerWithCode.setCode("anycode");
        commonHelpers.createEntity(CUSTOMERS_PATH, customerWithCode);
        responseIsUnprocessableEntity();
    }

    @Test
    public void customerCodeCannotBeUpdatedManually() throws Exception {
        UUID createdCustomerId = commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        CustomerDto customerWithCode = new CustomerDto();
        customerWithCode.setCode("anycode");
        commonHelpers.updateEntity(CUSTOMERS_PATH, createdCustomerId, customerWithCode);
        responseIsUnprocessableEntity();
    }

    @Test
    @FileParameters(EXAMPLES + "correctGeneratedCustomerCodeTestExamples.csv")
    public void correctCustomerCodeIsReturnedBasedOnAddress(String name, String line1, String city, String zipCode, String countryCode, String resultCode) throws Exception {
        AddressDto address = commonHelpers.constructAddressDto(line1, city, zipCode, countryCode);
        testCustomer1.setName(name);
        testCustomer1.setVatId(null);
        testCustomer1.setAddress(address);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("customer_code", resultCode);
    }

    @Test
    public void smallestIntIsConcatenatedToNonUniqueCustomerCode() throws Exception {
        AddressDto address = commonHelpers.constructAddressDto("line 1","Brno", "60200", "CZ");
        testCustomer1.setName("Hilton");
        testCustomer1.setVatId(null);
        testCustomer1.setId(null);
        testCustomer1.setAddress(address);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("customer_code", "CZBRQHIL");

        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("customer_code", "CZBRQHIL1");

        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("customer_code", "CZBRQHIL2");
    }
}
