package travel.snapshot.dp.qa.junit.tests.identity.users;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.List;
import java.util.Map;

@Tag(SLOW_TEST)
class UserParametrizedTests extends CommonTest {

    private static final String GET_USERS_EXAMPLES = "/csv/users/getUsers.csv";
    private static final Integer TOTAL_NUMBER_OF_USERS = 59;

    @BeforeAll
    static void setUpTestUsers() {
        DbStepDefs dbStepDefs = new DbStepDefs();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();

        range(0, 58).forEachOrdered(n -> {
            testUser2.setId(null);
            testUser2.setUsername(String.format("backgroundUser-%d", n));
            testUser2.setEmail(String.format("backgroundEmail-%d@snapshot.travel", n));
            entityIsCreated(testUser2);
        });
    }

    @Override
    @BeforeEach
    public void setUp() {
        // Override CommonTest setup and don't delete all created partners
    }

    @ParameterizedTest
    @CsvFileSource(resources = GET_USERS_EXAMPLES)
    void filteringUsers(String limit, String cursor, Integer returned, String linkHeader) {
        Map<String, String> queryParams = QueryParams.builder().limit(limit).cursor(cursor).build();
        List<UserDto> returnedUsers = stream(getEntities(USERS_PATH, queryParams)
                .then()
                .statusCode(SC_OK)
                .assertThat()
                .header(TOTAL_COUNT_HEADER, is(TOTAL_NUMBER_OF_USERS.toString()))
                .header("Link", containsString(linkHeader))
                .extract().response().as(UserDto[].class))
                .collect(toList());
        assertThat(returnedUsers.size()).isEqualTo(returned);
    }

}
