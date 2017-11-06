package travel.snapshot.dp.qa.junit.helpers;


import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.transformNull;

import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;

@Log
public class CustomerHelpers extends CustomerSteps {

    public CustomerHelpers() {
        super();
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
}
