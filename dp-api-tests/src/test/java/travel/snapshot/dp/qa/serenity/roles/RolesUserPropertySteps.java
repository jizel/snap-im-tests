package travel.snapshot.dp.qa.serenity.roles;

public class RolesUserPropertySteps extends RoleBaseSteps {

    public static final String USER_PROPERTY_ROLES_PATH = "/identity/user_property_roles";

    public RolesUserPropertySteps() {
        spec.basePath(USER_PROPERTY_ROLES_PATH);
    }

    @Override
    public String getBasePath() {
        return USER_PROPERTY_ROLES_PATH;
    }


//    private void replaceUserCustomerPathRelation(String userId, String customerId){
//        String path = USER_CUSTOMER_ROLES_PATH_RELATION;
//        path = path.replace("{uid}", userId);
//        path = path.replace("{cid}", customerId);
//
//        spec.basePath(path);
//    }
}
