package travel.snapshot.dp.qa.helpers;

import org.apache.commons.lang3.RandomStringUtils;
import travel.snapshot.dp.qa.model.Address;

/**
 * Created by sedlacek on 10/7/2015.
 */
public class AddressUtils {

    public static Address createRandomAddress(int streetLength, int cityLength, int zipLength, String country) {
        Address a = new Address();
        a.setAddressLine1(RandomStringUtils.randomAlphabetic(streetLength) + " " + RandomStringUtils.randomNumeric(3));
        a.setAddressLine2(RandomStringUtils.randomAlphabetic(streetLength) + " " + RandomStringUtils.randomNumeric(3));
        a.setCity(RandomStringUtils.randomAlphabetic(cityLength));
        a.setZipCode(RandomStringUtils.randomAlphanumeric(zipLength));
        a.setCountry(country);
        return a;
    }
}
