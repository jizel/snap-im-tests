package travel.snapshot.dp.qa.easyTests.converters;

import static java.util.logging.Level.INFO;
import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.getBooleanValue;
import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.getStringValue;

import lombok.extern.java.Log;
import org.easetech.easytest.converter.AbstractConverter;
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
@Log
public class CustomerConverter extends AbstractConverter<CustomerCreateDto> {

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

    private static final String COUNTRY = "country";
    private static final String ADDRESSLINE2 = "addressLine2";
    private static final String ADDRESSLINE1 = "addressLine1";
    private static final String CITY = "city";
    private static final String REGION = "region";
    private static final String ZIPCODE = "zipCode";


    @Override
    public CustomerCreateDto convert(Map<String, Object> yamlData) {
        CustomerCreateDto customer = null;

        if (!yamlData.containsKey(CUSTOMER_KEY)) {
            log.log(INFO, "Customer YAML converter was used but there is no key customer in the data!");
        }
        else {
            Map<String, Object> customerMap = (Map<String, Object>) yamlData.get(CUSTOMER_KEY);

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
        }
        return customer;
    }


    // Help methods
    private AddressDto getAddress(Map<String, Object> addressMap) {
        AddressDto address = new AddressDto();
        address.setAddressLine1(getStringValue(addressMap, ADDRESSLINE1));
        address.setAddressLine2(getStringValue(addressMap, ADDRESSLINE2));
        address.setCity(getStringValue(addressMap, CITY));
        address.setCountry(getStringValue(addressMap, COUNTRY));
        address.setRegion(getStringValue(addressMap, REGION));
        address.setZipCode(getStringValue(addressMap, ZIPCODE));

        return address;
    }

}
