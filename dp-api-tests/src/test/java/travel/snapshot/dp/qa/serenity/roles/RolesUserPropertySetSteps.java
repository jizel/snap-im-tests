package travel.snapshot.dp.qa.serenity.roles;

public class RolesUserPropertySetSteps extends RoleBaseSteps {

    public static final String USER_PROPERTY_SET_ROLES_PATH = "/identity/user_property_set_roles";

    public RolesUserPropertySetSteps() {
        spec.basePath(USER_PROPERTY_SET_ROLES_PATH);
    }

    @Override
    public String getBasePath() {
        return USER_PROPERTY_SET_ROLES_PATH;
    }
}
