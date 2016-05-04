package travel.snapshot.dp.qa.serenity.roles;

public class RolesUserCustomerSteps extends RoleBaseSteps {

    public static final String USER_CUSTOMER_ROLES_PATH = "/identity/user_customer_roles";

    public RolesUserCustomerSteps() {
        spec.basePath(USER_CUSTOMER_ROLES_PATH);
    }

    @Override
    public String getBasePath(){
        return USER_CUSTOMER_ROLES_PATH;
    }

}
