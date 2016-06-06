package travel.snapshot.dp.qa.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import travel.snapshot.dp.api.identity.model.AddressDto;

public class AddressUtils {

    public static AddressDto createRandomAddress(int streetLength, int cityLength, int zipLength, String country, String region) {
        AddressDto a = new AddressDto();

        a.setAddressLine1(RandomStringUtils.randomAlphabetic(streetLength) + " " + RandomStringUtils.randomNumeric(3));
        a.setAddressLine2(RandomStringUtils.randomAlphabetic(streetLength) + " " + RandomStringUtils.randomNumeric(3));
        a.setZipCode(RandomStringUtils.randomNumeric(zipLength));
        a.setCity(RandomStringUtils.randomAlphabetic(cityLength));

        if (country != null && !country.isEmpty()) {
            a.setCountry(country);
        }

        if (region != null && !region.isEmpty()) {
            a.setRegion(region);
        }

        return a;
    }
}
