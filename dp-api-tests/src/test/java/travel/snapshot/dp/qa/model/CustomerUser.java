package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-19T14:32:40.043+03:00")
public class CustomerUser {
  
  private String userId = null;
  private String userName = null;
  private Boolean isPrimary = null;

  
  /**
   * Unique identifier of the user
   **/
  @ApiModelProperty(required = true, value = "Unique identifier of the user")
  @JsonProperty("user_id")
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  @JsonProperty("user_name")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Indicates whether the relationship is primary (user primarily belongs to the given customer).
   **/
  @ApiModelProperty(required = true, value = "Indicates whether the relationship is primary (user primarily belongs to the given customer).")
  @JsonProperty("is_primary")
  public Boolean getIsPrimary() {
    return isPrimary;
  }
  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerUser {\n");
    
    sb.append("  userId: ").append(userId).append("\n");
    sb.append("  isPrimary: ").append(isPrimary).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
