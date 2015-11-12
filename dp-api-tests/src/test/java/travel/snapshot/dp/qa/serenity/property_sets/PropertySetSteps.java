package travel.snapshot.dp.qa.serenity.property_sets;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Property;
import travel.snapshot.dp.qa.model.PropertySet;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author martin.konkol(at)snapshot.travel
 * Created by Martin Konkol on 9/23/2015.
 */
public class PropertySetSteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTY_SETS = "property_sets";
    private static final String SERENITY_SESSION__CREATED_PROPERTY_SET = "created_property_set";
    private static final String SERENITY_SESSION__PROPERTY_SET_ID = "property_set_id";

    private static final String BASE_PATH__PROPERTY_SETS = "/identity/property_sets";

    public PropertySetSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH__PROPERTY_SETS);
    }
    
    // --- steps ---

    @Step
    public void followingPropertySetsExistForCustomer(List<PropertySet> propertySets, Customer customer) {
        propertySets.forEach(t -> {
            // remove duplicates
            PropertySet existingPropertySet = getPropertySetByNameForCustomer(t.getPropertySetName(), customer.getCustomerId());
            if (existingPropertySet != null) {
                deleteEntity(existingPropertySet.getPropertySetId());
            }

            t.setCustomerId(customer.getCustomerId());
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != 201) {
                fail("Property set cannot be created: " + createResponse.asString());
            }
        });
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTY_SETS).to(propertySets);
    }

    private PropertySet getPropertySetByNameForCustomer(String propertySetName, String customerId) {
        String filter = String.format("property_set_name==%s and customer_id==%s", propertySetName, customerId);
        PropertySet[] properties = getEntities("100", "0", filter, null, null).as(PropertySet[].class);
        return Arrays.asList(properties).stream().findFirst().orElse(null);
    }

/*
    @Step
    public void getPropertyByCodeUsingEtag(String code) {
        Property propertyFromList = getPropertyByCodeInternal(code);
        if (propertyFromList == null) {
            fail("No matching property with code: [" + code + "] found.");
        }

        // we first need to get current ETag of a property
        Response responseWithETag = getProperty(propertyFromList.getPropertyId(), null);

        // try to get the property with current ETag
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader("ETag"));
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }
    
    @Step
    public void getPropertyWithCodeUsingEtagAfterUpdate(String code) {
        Property propertyFromList = getPropertyByCodeInternal(code);
        if (propertyFromList == null) {
            fail("No matching property with code: [" + code + "] found.");
        }

        // we first need to get current ETag of a property
        Response responseWithETag = getProperty(propertyFromList.getPropertyId(), null);

        // force new ETag on server side
        Response updateResponse = updateProperty(
                propertyFromList.getPropertyId(),
                Collections.singletonMap("vat_id", "CZ99999999"),
                responseWithETag.getHeader("ETag"));
        if (updateResponse.getStatusCode() != 204) {
            fail("Property cannot be updated: " + updateResponse.asString());
        }

        // get with old ETag
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader("ETag"));
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }
    

    @Step
    public void deletePropertyWithCode(String code) {
        String propertyId = getPropertyByCodeInternal(code).getPropertyId();
        Response resp = deleteProperty(propertyId);

        //store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTY_ID).to(propertyId);
    }

    @Step
    public void propertyIdInSessionDoesntExist() {
        String propertyId = Serenity.sessionVariableCalled(SERENITY_SESSION__PROPERTY_ID);

        Response response = getProperty(propertyId, null);
        response.then().statusCode(404);
    }
    
    */

    public void deleteAllPropertySetsForCustomer(List<Customer> customers) {
        customers.forEach(c -> {
            String filter = "customer_id==" + c.getCustomerId();
            Response entities = getEntities("100000", "0", filter, null, null);
            PropertySet[] propertySets = entities.as(PropertySet[].class);
            for (PropertySet ps : propertySets) {
                Response deleteResponse = deleteEntity(ps.getPropertySetId());
                if (deleteResponse.statusCode() != 204) {
                    fail("Property set cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    public void listOfPropertySetsIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void propertySetNamesAreInResponseInOrder(List<String> names) {
        Response response = getSessionResponse();
        PropertySet[] propertySets = response.as(PropertySet[].class);
        int i = 0;
        for (PropertySet ps : propertySets) {
            assertEquals("Property set on index=" + i + " is not expected", names.get(i), ps.getPropertySetName());
            i++;
        }
    }
}
