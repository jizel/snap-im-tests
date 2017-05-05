package travel.snapshot.dp.qa.easyTests.converters;

import static java.util.logging.Level.INFO;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType;
import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.getStringValue;

import lombok.extern.java.Log;
import org.easetech.easytest.converter.AbstractConverter;
import travel.snapshot.dp.api.identity.model.UserCreateDto;

import java.util.Map;
import java.util.Optional;

/**
 * Converter for Data Platform API UserCreateDto object. It maps strings from YAML formatted tests data set to the User object.
 *
 * To be used in tests with @Converters({UserConverter.class}) annotation.
 */
@Log
public class UserConverter extends AbstractConverter<UserCreateDto> {

    private static final String USER_ID = "id";
    private static final String USER_NAME = "userName";
    private static final String USER_TYPE = "userType";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String CULTURE = "culture";
    private static final String TIMEZONE = "timezone";
    private static final String USER_EMAIL = "email";
    private static final String USER_KEY = "user";

    @Override
    public UserCreateDto convert(Map<String, Object> convertFrom) {
        UserCreateDto user = null;

        if (!convertFrom.containsKey(USER_KEY)) {
            log.log(INFO, "Customer YAML converter was used but there is no key customer in the data!");
        }
        else {
            Map<String, Object> userMap = (Map<String, Object>) convertFrom.get(USER_KEY);
            user = new UserCreateDto();

            user.setId(getStringValue(userMap, USER_ID));
            user.setUserName(getStringValue(userMap, USER_NAME));
            UserType userType = Optional.ofNullable(getStringValue(userMap, USER_TYPE)).map(UserType::valueOf).orElse(null);
            user.setUserType(userType);
            user.setFirstName(getStringValue(userMap, FIRST_NAME));
            user.setLastName(getStringValue(userMap, LAST_NAME));
            user.setCulture(getStringValue(userMap, CULTURE));
            user.setTimezone(getStringValue(userMap, TIMEZONE));
            user.setEmail(getStringValue(userMap, USER_EMAIL));
        }
        return user;
    }
}
