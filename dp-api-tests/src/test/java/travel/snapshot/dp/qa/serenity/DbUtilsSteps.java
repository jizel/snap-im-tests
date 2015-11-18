package travel.snapshot.dp.qa.serenity;

import travel.snapshot.dp.qa.helpers.DbHelper;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class DbUtilsSteps {

    public static final String DELETE_CUSTOMER_PROPERTY = "delete from customer_property where customer_id = ? and property_id = ?";
    DbHelper dbHelper = new DbHelper();

    public void deleteAllPropertyCustomersFromDb(String customerId, String propertyId) {
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY, customerId, propertyId);
    }


}
