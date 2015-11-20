package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-11-10T15:16:08.971+02:00")
public class PropertyUser {

    private String userId = null;
    private String userName = null;

    @JsonProperty("user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    /**
     * Unique identifier of the user.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the user.")
    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PropertyUser {\n");

        sb.append("  userId: ").append(userId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
