package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-28T12:38:55.027+02:00")
public class Role {
  
  private String roleId = null;
  private String applicationId = null;
  private String roleName = null;
  private String roleDescription = null;

  
  /**
   * Unique identifier of the role in GUID format.
   **/
  @ApiModelProperty(value = "Unique identifier of the role in GUID format.")
  @JsonProperty("role_id")
  public String getRoleId() {
    return roleId;
  }
  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  
  /**
   * Unique reference to Applcation
   **/
  @ApiModelProperty(required = true, value = "Unique reference to Applcation")
  @JsonProperty("application_id")
  public String getApplicationId() {
    return applicationId;
  }
  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  
  /**
   * The name of the role
   **/
  @ApiModelProperty(required = true, value = "The name of the role")
  @JsonProperty("role_name")
  public String getRoleName() {
    return roleName;
  }
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  
  /**
   * Description of the purpose of the role
   **/
  @ApiModelProperty(value = "Description of the purpose of the role")
  @JsonProperty("role_description")
  public String getRoleDescription() {
    return roleDescription;
  }
  public void setRoleDescription(String roleDescription) {
    this.roleDescription = roleDescription;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Role {\n");
    
    sb.append("  roleId: ").append(roleId).append("\n");
    sb.append("  applicationId: ").append(applicationId).append("\n");
    sb.append("  roleName: ").append(roleName).append("\n");
    sb.append("  roleDescription: ").append(roleDescription).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
