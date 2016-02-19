package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-11-10T10:53:54.555+02:00")
public class UserRole {

    private String roleId = null;
    private String roleName = null;
    private String relationshipId = null;

    public enum RelationshipTypeEnum {
        customer, property, property_set,
    }

    ;
    private RelationshipTypeEnum relationshipType = null;

    /**
     * The name of the role.
     **/
    @ApiModelProperty(required = true, value = "The name of the role.")
    @JsonProperty("role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Unique identifier of the role.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the role.")
    @JsonProperty("role_id")
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


    /**
     * Unique identifier of the entity.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the entity.")
    @JsonProperty("relationship_id")
    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }


    /**
     * Type of the relationship
     **/
    @ApiModelProperty(required = true, value = "Type of the relationship")
    @JsonProperty("relationship_type")
    public RelationshipTypeEnum getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipTypeEnum relationshipType) {
        this.relationshipType = relationshipType;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class UserRole {\n");

        sb.append("  roleId: ").append(roleId).append("\n");
        sb.append("  relationshipId: ").append(relationshipId).append("\n");
        sb.append("  relationshipType: ").append(relationshipType).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
