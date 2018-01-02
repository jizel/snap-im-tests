package travel.snapshot.dp.qa.junit.tests.identity.user_groups;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_MESSAGE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class UserGroupTests extends CommonTest {

    private static final String FILTERING_USER_GROUPS_EXAMPLES = "/csv/user_groups/filteringUserGroups.csv";
    private static final Integer NO_OF_GENERATED_USER_GROUPS = 52;

    @Test
    void userGroupCRUD() {
        UserGroupDto createdUserGroup = entityIsCreatedAs(UserGroupDto.class, testUserGroup1);
        compareUserGroups(createdUserGroup, testUserGroup1);

        UserGroupUpdateDto userGroupUpdate = getUserGroupUpdate();
        entityIsUpdated(USER_GROUPS_PATH, createdUserGroup.getId(), userGroupUpdate);

        compareUserGroups(getEntityAsType(USER_GROUPS_PATH, UserGroupDto.class, createdUserGroup.getId()), userGroupUpdate);

        entityIsDeleted(USER_GROUPS_PATH, createdUserGroup.getId());
        getEntity(USER_GROUPS_PATH, createdUserGroup.getId()).then().statusCode(SC_NOT_FOUND);
    }

    @Test
    void customerMustExistForUserGroup() {
        testUserGroup1.setCustomerId(NON_EXISTENT_ID);
        createEntity(testUserGroup1).then()
                .statusCode(SC_NOT_FOUND)
                .body(RESPONSE_MESSAGE, is("Resource Customer with ID " + NON_EXISTENT_ID + " was not found."));
    }

    @ParameterizedTest
    @CsvFileSource(resources = FILTERING_USER_GROUPS_EXAMPLES)
    void filteringUserGroups(String limit, String cursor, String filter, String sort, String sort_desc, String returned, String totalCount) {
        generateUserGroups(NO_OF_GENERATED_USER_GROUPS);
        Map<String, String> queryParams = QueryParams.builder().limit(limit).cursor(cursor).filter(filter).sort(sort).sortDesc(sort_desc).build();
        List<UserGroupDto> returnedUserGroups = stream(getEntities(USER_GROUPS_PATH, queryParams)
                .then()
                .statusCode(SC_OK)
                .assertThat()
                .header(TOTAL_COUNT_HEADER, Matchers.is(totalCount))
                .extract().response().as(UserGroupDto[].class))
                .collect(toList());
        assertThat(returnedUserGroups.size()).isEqualTo(Integer.valueOf(returned));
    }

    private static void compareUserGroups(UserGroupDto userGroupDto, UserGroupUpdateDto userGroupUpdateDto) {
        assertAll(
                () -> assertEquals(userGroupDto.getName(), userGroupUpdateDto.getName()),
                () -> assertEquals(userGroupDto.getDescription(), userGroupUpdateDto.getDescription()),
                () -> assertEquals(userGroupDto.getIsActive(), userGroupUpdateDto.getIsActive()),
                () -> assertEquals(userGroupDto.getPicture(), userGroupUpdateDto.getPicture())
        );
        if (userGroupUpdateDto.getCustomerId() != null) {
            assertEquals(userGroupDto.getCustomerId(), userGroupUpdateDto.getCustomerId());
        }
    }

    private UserGroupUpdateDto getUserGroupUpdate() {
        UserGroupUpdateDto userGroupUpdate = new UserGroupUpdateDto();
        userGroupUpdate.setName("Updated name");
        userGroupUpdate.setDescription("Updated Description");
        userGroupUpdate.setPicture("http://updated.picture.com");
        userGroupUpdate.setIsActive(false);

        UUID createdCustomerId = entityIsCreated(testCustomer1);
        userGroupUpdate.setCustomerId(createdCustomerId);

        return userGroupUpdate;
    }

    private void generateUserGroups(int count) {
        range(0, count).forEachOrdered(n -> {
            testUserGroup2.setId(null);
            testUserGroup2.setName(String.format("generatedUserGroup-%s", n));
            testUserGroup2.setDescription(String.format("generatedUserGroup Description %s", n));
            entityIsCreated(testUserGroup2);
        });
    }
}
