package travel.snapshot.dp.qa.junit.tests.identity.roles;

import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByName;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;

/**
 * GET, filtering, sorting etc. tests for Role entity. 50+ roles created for testing (pagination)
 */
@Category(Categories.SlowTests.class)
public class RolesGetTests extends CommonTest{

    private static final String EXAMPLES = "/csv/roles/";
    private static DbStepDefs dbStepDefs;

    @BeforeAll
    public static void createTestRoles() throws Exception {
//        Create 50+ test roles but only once for all tests!
        dbStepDefs = new DbStepDefs();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
        range(0, 52).forEachOrdered(n -> {
            testRole1.setName(String.format("role_%d", n));
            entityIsCreated(testRole1);
        });
        range(0, 2).forEachOrdered(n -> {
            String roleName = String.format("role_%d", n);
            RoleUpdateDto update = new RoleUpdateDto();
            update.setDescription(String.format("description_%d", n));
            update.setIsInitial(true);
            RoleDto createdRole = getEntityByName(ROLES_PATH, roleName);
            updateEntity(ROLES_PATH, createdRole.getId(), update);
        });
    }

    @Override
    @BeforeEach
    public void setUp() {
        // Override CommonTest setup and don't delete all created roles
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "gettingListOfRoles.csv")
    public void gettingListOfRoles(String limit, String cursor, Integer returned, Integer total, String linkHeader) {
        getEntities(ROLES_PATH, buildQueryParamMapForPaging(limit, cursor, null, null, null, null));
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(RoleDto.class, returned);
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
        if (! linkHeader.equals("/null")) {
            headerContains("Link", linkHeader);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfRoles.csv")
    public void filteringListOfRoles(String limit, String cursor, Integer returned, Integer total, String filter) {
        getEntities(ROLES_PATH, buildQueryParamMapForPaging(limit, cursor, filter, null, null, null));
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(RoleDto.class, returned);
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
    }
}