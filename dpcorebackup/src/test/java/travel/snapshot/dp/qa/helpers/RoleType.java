package travel.snapshot.dp.qa.cucumber.helpers;

import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;

public enum RoleType {

    CUSTOMER(CustomerRoleDto.class),
    PROPERTY(PropertyRoleDto.class);

    private final Class<? extends RoleBaseDto> dtoClassType;

    RoleType(Class<? extends RoleBaseDto> dtoClassType) {
        this.dtoClassType = dtoClassType;
    }

    public Class<? extends RoleBaseDto> getDtoClassType() {
        return dtoClassType;
    }

}
