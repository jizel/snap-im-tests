package travel.snapshot.dp.qa.junit.tests.identity.users;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Tests for user type based restrictions
 */

@RunWith(SerenityRunner.class)
public class userRestrictionTests extends CommonTest {

    @Jira("DP-1985")
    @Test
    public void userPasswordRestrictionTest() throws Exception {
        UserDto customerUser1 = userHelpers.userIsCreated(testUser1);
        UserDto customerUser2 = userHelpers.userIsCreated(testUser2);
        UserDto partnerUser = userHelpers.userIsCreated(testUser3);
//        Valid cases - snapshot user and user himself
        userHelpers.setUserPassword(customerUser1.getId(), "newPassword");
        responseCodeIs(SC_NO_CONTENT);
        userHelpers.setUserPasswordByUser(customerUser1.getId(), customerUser1.getId(), "NewPasswordSetByUserHimself");
        responseCodeIs(SC_NO_CONTENT);
//        Invalid cases - other users
        userHelpers.setUserPasswordByUser(customerUser2.getId(), customerUser1.getId(), "otherCustomerUserCannotSetNewPassword");
        responseCodeIs(SC_FORBIDDEN);
        customCodeIs(INSUFFICIENT_PERMISSIONS_CUSTOM_CODE);
        userHelpers.setUserPasswordByUser(partnerUser.getId(), customerUser1.getId(), "otherCustomerUserCannotSetNewPassword");
        responseCodeIs(SC_FORBIDDEN);
        customCodeIs(INSUFFICIENT_PERMISSIONS_CUSTOM_CODE);
    }
}
