package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.assertIfNotNull;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;

import com.jayway.restassured.response.Response;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Parametrized test for CRUD of Customer entity - error codes etc.
 */
@RunWith(JUnitParamsRunner.class)
public class CustomerParametersCRUDTests extends CommonTest{

    private static final String EXAMPLES = "src/test/resources/csv/customers/";

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @FileParameters(EXAMPLES + "errorCodesCreateCustomer.csv")
    public void errorCodesCreateCustomer(String jsonFileName) throws Exception {
        commonHelpers.useFileForSendDataTo(jsonFileName, POST.toString(), CUSTOMERS_PATH, "identity");
        responseIsUnprocessableEntity();
    }

    @Test
    @FileParameters(EXAMPLES + "foreignCustomers.csv")
    public void createForeignCustomer(String jsonFileName, String foreignName) throws Exception {
        commonHelpers.useFileForSendDataTo(jsonFileName, POST.toString(), CUSTOMERS_PATH, "identity");
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("name", foreignName);
    }

    @Test
    @FileParameters(EXAMPLES + "updateEachField.csv")
    public void updateCustomerEachField(String name, String email, String vatId, String phone, String website, String notes, String timezone) {
        UUID createdCustomerId = entityIsCreated(testCustomer1);
        CustomerUpdateDto customerUpdate = customerHelpers.constructCustomerUpdate(name, email, vatId, phone, website, notes , timezone);
        Response updateResponse = updateEntity(CUSTOMERS_PATH, createdCustomerId, customerUpdate);
        responseCodeIs(SC_OK);

        CustomerDto updatedCustomer = updateResponse.as(CustomerDto.class);
//        If an attribute is set to null then it's not updated at all and original value is returned, therefore null check is needed.
        assertIfNotNull(updatedCustomer.getName(), name);
        assertIfNotNull(updatedCustomer.getEmail(), email);
        assertIfNotNull(updatedCustomer.getVatId(), vatId);
        assertIfNotNull(updatedCustomer.getPhone(), phone);
        assertIfNotNull(updatedCustomer.getWebsite(), website);
        assertIfNotNull(updatedCustomer.getNotes(), notes);
        assertIfNotNull(updatedCustomer.getTimezone(), timezone);
    }
}
