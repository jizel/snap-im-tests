package travel.snapshot.dp.qa.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import travel.snapshot.dp.api.identity.model.AddressDto;

public class AddressUtils {

    public static AddressDto createRandomAddress(int streetLength, int cityLength, int zipLength, String country) {
        AddressDto a = new AddressDto();
        a.setAddressLine1(RandomStringUtils.randomAlphabetic(streetLength) + " " + RandomStringUtils.randomNumeric(3));
        a.setAddressLine2(RandomStringUtils.randomAlphabetic(streetLength) + " " + RandomStringUtils.randomNumeric(3));
        a.setCity(RandomStringUtils.randomAlphabetic(cityLength));
        a.setZipCode(RandomStringUtils.randomAlphanumeric(zipLength));
        a.setCountry(country);
        return a;
    }
}
