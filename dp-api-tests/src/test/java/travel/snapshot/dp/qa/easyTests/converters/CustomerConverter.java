package travel.snapshot.dp.qa.easyTests.converters;

import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.getAddress;
import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.getBooleanValue;
import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.getStringValue;

import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.type.SalesforceId;

import java.util.Map;
import java.util.Optional;

/**
 * Converter for Data Platform API CustomerCreateDto object. It maps strings from YAML formatted tests data set to the Customer object.
 *
 * To be used in tests with @Converters({CustomerConverter.class}) annotation.
 */
public class CustomerConverter {

    private static final String IS_ACTIVE = "isActive";
    private static final String PARENT_ID = "parentId";
    private static final String HOSPITALITY_ID = "hospitalityId";
    private static final String SALESFORCE_ID = "salesforceId";
    private static final String PHONE = "phone";
    private static final String CUSTOMER_EMAIL = "email";
    private static final String VAT_ID = "vatId";
    private static final String COMPANY_NAME = "companyName";
    private static final String ID = "id";
    private static final String WEBSITE = "website";
    private static final String IS_DEMO_CUSTOMER = "isDemoCustomer";
    private static final String NOTES = "notes";
    private static final String HEADQUARTERS_TIMEZONE = "timezone";
    private static final String TYPE = "type";

    private static final String CUSTOMER_KEY = "customer";
    private static final String ADDRESS_KEY = "address";


    public static CustomerCreateDto convert(Object yamlData) {
        CustomerCreateDto customer = null;

            Map<String, Object> customerMap = (Map<String, Object>) yamlData;

            customer = new CustomerCreateDto();

            customer.setIsActive(getBooleanValue(customerMap, IS_ACTIVE));
            customer.setParentId(getStringValue(customerMap, PARENT_ID));
            customer.setHospitalityId(getStringValue(customerMap, HOSPITALITY_ID));
            SalesforceId customerSalesForceId = Optional.ofNullable(getStringValue(customerMap, SALESFORCE_ID)).map(SalesforceId::of).orElse(null);
            customer.setSalesforceId(customerSalesForceId);
            customer.setPhone(getStringValue(customerMap, PHONE));
            customer.setEmail(getStringValue(customerMap, CUSTOMER_EMAIL));
            customer.setCompanyName(getStringValue(customerMap, COMPANY_NAME));
            customer.setId(getStringValue(customerMap, ID));
            customer.setVatId(getStringValue(customerMap, VAT_ID));
            customer.setWebsite(getStringValue(customerMap, WEBSITE));
            customer.setIsDemoCustomer(getBooleanValue(customerMap, IS_DEMO_CUSTOMER));
            customer.setNotes(getStringValue(customerMap, NOTES));
            customer.setTimezone(getStringValue(customerMap, HEADQUARTERS_TIMEZONE));
            CustomerType customerType = Optional.ofNullable(getStringValue(customerMap, TYPE)).map(CustomerType::valueOf).orElse(null);
            customer.setType(customerType);
            if (customerMap.containsKey(ADDRESS_KEY)) {
                AddressDto address = getAddress((Map<String, Object>) customerMap.get(ADDRESS_KEY));
                customer.setAddress(address);
            }
        return customer;
    }


    // Help methods


}
