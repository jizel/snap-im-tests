package travel.snapshot.dp.qa.easyTests.tests;

import static java.util.logging.Level.INFO;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.easetech.easytest.converter.ConverterManager.cleanConverters;

import lombok.extern.java.Log;
import net.thucydides.core.annotations.Narrative;
import org.easetech.easytest.annotation.Converters;
import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.loader.LoaderType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.qa.easyTests.converters.CustomerConverter;
import travel.snapshot.dp.qa.easyTests.converters.UserConverter;
import travel.snapshot.dp.qa.easyTests.loaders.YamlLoader;
import travel.snapshot.dp.qa.easyTests.runners.DataDrivenSerenityRunner;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;
import travel.snapshot.dp.qa.steps.DbStepDefs;

/**
 * Created by zelezny on 5/16/2017.
 */

@Log
@Narrative(text = {"Short test class description", "Should be visible in serenity test results"})
@RunWith(DataDrivenSerenityRunner.class)
@DataLoader(filePaths = {"yaml/sampleTestData.yaml"}, loaderType = LoaderType.CUSTOM, loader = YamlLoader.class)
public class EasyTestYamlCustomer {

    private static CustomerSteps customerSteps = new CustomerSteps();
    private static UsersSteps usersSteps = new UsersSteps();
    private static BasicSteps basicSteps = new BasicSteps();
    private static DbStepDefs dbStepDefs = new DbStepDefs();


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        log.log(INFO, "DB was cleaned and default entities were created using sql");
    }

    @After
    public void cleanUp() {
        // If Converters are used once in the class, they stay registered and then mess up test using jsonObjects - those should not use converters (or should use specialized ones).
        cleanConverters();
    }


    @Test
    public void createCustomerUsingParams(@Param(name = "customer_id") String id,
                                          @Param(name = "company_name") String companyName,
                                          @Param(name = "customer_email") String email,
                                          @Param(name = "salesforce_id") String salesforceId,
                                          @Param(name = "vatId") String vatId,
                                          @Param(name = "isDemoCustomer") Boolean isDemoCustomer,
                                          @Param(name = "phone") String phone,
                                          @Param(name = "website") String website,
                                          @Param(name = "headquarters_timezone") String timezone,
                                          @Param(name = "type") String type,
                                          @Param(name = "hospitality_id") String hospitalityId) {
        CustomerCreateDto customer = new CustomerCreateDto();
        customer.setEmail(email);
        customer.setCompanyName(companyName);
        customer.setId(id);
        customer.setVatId(vatId);
        customer.setWebsite(website);
        customer.setIsDemoCustomer(isDemoCustomer);
        customer.setTimezone(timezone);
        customer.setType(CustomerType.valueOf(type));
        customer.setIsDemoCustomer(false);
        customerSteps.followingCustomerIsCreatedWithRandomAddress(customer);
        basicSteps.responseCodeIs(SC_CREATED);
    }

    @Test
    @Converters({CustomerConverter.class})
    public void createCustomerUsingConverter(CustomerCreateDto customerCreate) {
        customerSteps.followingCustomerIsCreatedWithRandomAddress(customerCreate);
        basicSteps.responseCodeIs(SC_CREATED);
        basicSteps.bodyContainsEntityWith("customer_id", customerCreate.getId());
    }

    @Test
    @Converters({CustomerConverter.class, UserConverter.class})
    public void createCustomerAndUserUsingConverters(CustomerCreateDto customerCreate, UserCreateDto userCreate) {
        customerSteps.followingCustomerIsCreatedWithRandomAddress(customerCreate);
        basicSteps.responseCodeIs(SC_CREATED);
        basicSteps.bodyContainsEntityWith("customer_id", customerCreate.getId());
        usersSteps.createUserWithCustomer(userCreate, customerCreate.getId(), true, true);
        basicSteps.responseCodeIs(SC_CREATED);
        basicSteps.bodyContainsEntityWith("user_id");
    }

    @Test
    @Converters({CustomerConverter.class, UserConverter.class})
    public void createCustomersAndUsersUsingConverters(CustomerCreateDto customer, UserCreateDto user) {
        customerSteps.followingCustomerIsCreated(customer);
        basicSteps.responseCodeIs(SC_CREATED);
        CustomerDto createdCustomer = basicSteps.getSessionResponse().as(CustomerDto.class);
        usersSteps.createUserWithCustomer(user, createdCustomer.getId(), true, true);
        basicSteps.responseCodeIs(SC_CREATED);
    }

}
