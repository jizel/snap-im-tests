package travel.snapshot.dp.qa.helpers;

import org.apache.commons.lang3.RandomStringUtils;

import travel.snapshot.dp.api.identity.model.CustomerCreateDto;

/**
 * Created by vlcek on 5/26/2016.
 */
public class CustomerUtils {

    public static CustomerCreateDto createRandomCustomer() {
        return createRandomCustomer(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + "." + RandomStringUtils.randomAlphabetic(2),
                RandomStringUtils.randomAlphabetic(10),
                "CZ" + RandomStringUtils.randomNumeric(8),
                Boolean.TRUE,
                "+420" + RandomStringUtils.randomNumeric(9),
                "http://www.snapshot.travel",
                "Europe/Prague"
        );
    }

    public static CustomerCreateDto createRandomCustomer(String vatId) {
        return createRandomCustomer(
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + "." + RandomStringUtils.randomAlphabetic(2),
                RandomStringUtils.randomAlphabetic(10),
                vatId,
                Boolean.TRUE,
                "+420" + RandomStringUtils.randomNumeric(9),
                "http://www.snapshot.travel",
                "Europe/Prague"
        );
    }

    public static CustomerCreateDto createRandomCustomer(String companyName, String email, String salesForce, String vatId, Boolean demoCustomer, String phone, String website, String timezone) {
        CustomerCreateDto c = new CustomerCreateDto();
        c.setCompanyName(companyName);
        c.setEmail(email);
        c.setSalesforceId(salesForce);
        c.setVatId(vatId);
        c.setIsDemoCustomer(demoCustomer);
        c.setPhone(phone);
        c.setWebsite(website);
        c.setTimezone(timezone);
        return c;
    }

}
