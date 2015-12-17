package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-27T15:59:47.479+02:00")
public class User {

    public enum UserTypeEnum {
        snapshot, customer, partner, guest,
    }

    ;

    private String userId = null;
    private String partnerId = null;
    private String salesforceId = null;
    private UserTypeEnum userType = null;
    private String userName = null;
    private String firstName = null;
    private String lastName = null;
    private String phone = null;
    private String email = null;
    private String timezone = null;
    private String culture = null;
    private String comment = null;
    private String picture = null;
    private Integer isActive = null;

    /**
     * Unique identifier of the user in GUID format.
     **/
    @ApiModelProperty(value = "Unique identifier of the user in GUID format.")
    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Reference id to the partner
     **/
    @ApiModelProperty(value = "Reference id to the partner")
    @JsonProperty("partner_id")
    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    /**
     * Salesforce unique identifier.
     **/
    @ApiModelProperty(value = "Salesforce unique identifier.")
    @JsonProperty("salesforce_id")
    public String getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(String salesforceId) {
        this.salesforceId = salesforceId;
    }

    /**
     * Type of the user.
     **/
    @ApiModelProperty(required = true, value = "Type of the user.")
    @JsonProperty("user_type")
    public UserTypeEnum getUserType() {
        return userType;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }

    /**
     * User name (unique).
     **/
    @ApiModelProperty(required = true, value = "User name (unique).")
    @JsonProperty("user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * First name
     **/
    @ApiModelProperty(required = true, value = "First name")
    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Last name
     **/
    @ApiModelProperty(required = true, value = "Last name")
    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * User phone number
     **/
    @ApiModelProperty(value = "User phone number")
    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * User e-mail address
     **/
    @ApiModelProperty(required = true, value = "User e-mail address")
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * User timezone in the structure containing city and timezone info
     **/
    @ApiModelProperty(required = true, value = "User timezone in the structure containing city and timezone info")
    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * ?Culture code
     **/
    @ApiModelProperty(required = true, value = "?Culture code")
    @JsonProperty("culture")
    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    /**
     * Comment/notes for the given user.
     **/
    @ApiModelProperty(value = "Comment/notes for the given user.")
    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Link to the picture
     **/
    @ApiModelProperty(value = "Link to the picture")
    @JsonProperty("picture")
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Indicates whether the user is active
     **/
    @ApiModelProperty(value = "Indicates whether the user is active")
    @JsonProperty("is_active")
    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class User {\n");

        sb.append("  userId: ").append(userId).append("\n");
        sb.append("  partnerId: ").append(partnerId).append("\n");
        sb.append("  salesforceId: ").append(salesforceId).append("\n");
        sb.append("  userType: ").append(userType).append("\n");
        sb.append("  userName: ").append(userName).append("\n");
        sb.append("  firstName: ").append(firstName).append("\n");
        sb.append("  lastName: ").append(lastName).append("\n");
        sb.append("  phone: ").append(phone).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  timezone: ").append(timezone).append("\n");
        sb.append("  culture: ").append(culture).append("\n");
        sb.append("  comment: ").append(comment).append("\n");
        sb.append("  picture: ").append(picture).append("\n");
        sb.append("  isActive: ").append(isActive).append("\n");
        sb.append("}\n");

        return sb.toString();
    }
}
