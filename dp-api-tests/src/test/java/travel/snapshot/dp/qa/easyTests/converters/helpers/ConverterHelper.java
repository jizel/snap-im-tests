package travel.snapshot.dp.qa.easyTests.converters.helpers;

import travel.snapshot.dp.api.identity.model.AddressDto;

import java.util.Map;

/**
 * Help methods for object converters
 */
public class ConverterHelper {
    private static final String COUNTRY = "country";
    private static final String ADDRESSLINE2 = "addressLine2";
    private static final String ADDRESSLINE1 = "addressLine1";
    private static final String CITY = "city";
    private static final String REGION = "region";
    private static final String ZIPCODE = "zipCode";


    public static String getStringValue(Map valueMap, String key) {
        if (valueMap.get(key) == null) {
            return null;
        }
        return String.valueOf(valueMap.get(key));
    }

    public static Boolean getBooleanValue(Map valueMap, String key) {
        if (valueMap.get(key) == null) {
            return null;
        }
        return Boolean.valueOf(getStringValue(valueMap, key));
    }
    public static AddressDto getAddress(Map<String, Object> addressMap) {
        AddressDto address = new AddressDto();
        address.setLine1(getStringValue(addressMap, ADDRESSLINE1));
        address.setLine2(getStringValue(addressMap, ADDRESSLINE2));
        address.setCity(getStringValue(addressMap, CITY));
        address.setCountryCode(getStringValue(addressMap, COUNTRY));
        address.setRegion(getStringValue(addressMap, REGION));
        address.setZipCode(getStringValue(addressMap, ZIPCODE));
        return address;
    }

}
