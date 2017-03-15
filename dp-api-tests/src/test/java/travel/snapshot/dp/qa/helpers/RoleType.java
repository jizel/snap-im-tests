package travel.snapshot.dp.qa.helpers;

import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.PropertySetRoleDto;
import travel.snapshot.dp.api.identity.model.RoleDto;

public enum RoleType {

    CUSTOMER(CustomerRoleDto.class),
    PROPERTY(PropertyRoleDto.class),
    PROPERTY_SET(PropertySetRoleDto.class);

    private final Class<? extends RoleDto> dtoClassType;

    RoleType(Class<? extends RoleDto> dtoClassType) {
        this.dtoClassType = dtoClassType;
    }

    public Class<? extends RoleDto> getDtoClassType() {
        return dtoClassType;
    }

}
