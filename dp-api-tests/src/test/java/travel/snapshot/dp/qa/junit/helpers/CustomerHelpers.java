package travel.snapshot.dp.qa.junit.helpers;


import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.transformNull;

import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;

import java.util.UUID;

@Log
public class CustomerHelpers extends BasicSteps {

    public CustomerHelpers() {
        super();
        spec.basePath(CUSTOMERS_PATH);
    }

    public CustomerUpdateDto constructCustomerUpdate(String name,
                                                     String email,
                                                     String vatId,
                                                     String phone,
                                                     String website,
                                                     String notes,
                                                     String timezone){
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName(transformNull(name));
        customerUpdate.setEmail(transformNull(email));
        customerUpdate.setVatId(transformNull(vatId));
        customerUpdate.setPhone(transformNull(phone));
        customerUpdate.setWebsite(transformNull(website));
        customerUpdate.setNotes(transformNull(notes));
        customerUpdate.setTimezone(transformNull(timezone));

        return customerUpdate;
    }

    public void listOfCustomerPropertiesIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, PROPERTIES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, USERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

}
