package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.assertIfNotNull;

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
    public void setUp() throws Exception {
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
    public void updateCustomerEachField(String name, String email, String vatId, String phone, String website, String notes, String timezone, String hospitalityId) {
        UUID createdCustomerId = commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        CustomerUpdateDto customerUpdate = customerHelpers.constructCustomerUpdate(name, email, vatId, phone, website, notes , timezone, hospitalityId);
        Response updateResponse = commonHelpers.updateEntity(CUSTOMERS_PATH, createdCustomerId, customerUpdate);
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
        if (transformNull(hospitalityId) != null) {
            assertThat(updatedCustomer.getHospitalityId(), is(UUID.fromString(hospitalityId)));
        }
    }
}
