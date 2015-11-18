package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-27T10:58:41.952+02:00")
public class Property {

    private String propertyId = null;
    private String salesforceId = null;
    private String propertyName = null;
    private String propertyCode = null;
    private String website = null;
    private String email = null;
    private Boolean isDemoProperty = null;
    private Address address = null;
    private String timezone = null;


    /**
     * Unique identifier of the property in GUID format.
     **/
    @ApiModelProperty(value = "Unique identifier of the property in GUID format.")
    @JsonProperty("property_id")
    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }


    /**
     * Identifier of the property in Salesforce.
     **/
    @ApiModelProperty(value = "Identifier of the property in Salesforce.")
    @JsonProperty("salesforce_id")
    public String getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(String salesforceId) {
        this.salesforceId = salesforceId;
    }


    /**
     * Name of the property
     **/
    @ApiModelProperty(required = true, value = "Name of the property")
    @JsonProperty("property_name")
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    /**
     * ?Booking.com code of the property
     **/
    @ApiModelProperty(required = true, value = "?Booking.com code of the property")
    @JsonProperty("property_code")
    public String getPropertyCode() {
        return propertyCode;
    }

    public void setPropertyCode(String propertyCode) {
        this.propertyCode = propertyCode;
    }


    /**
     * Website of the property
     **/
    @ApiModelProperty(value = "Website of the property")
    @JsonProperty("website")
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    /**
     * Official e-mail of the property
     **/
    @ApiModelProperty(required = true, value = "Official e-mail of the property")
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Indicates whether the property is just for demo purposes.
     **/
    @ApiModelProperty(required = true, value = "Indicates whether the property is just for demo purposes.")
    @JsonProperty("is_demo_property")
    public Boolean getIsDemoProperty() {
        return isDemoProperty;
    }

    public void setIsDemoProperty(Boolean isDemoProperty) {
        this.isDemoProperty = isDemoProperty;
    }


    /**
     * Hotel address
     **/
    @ApiModelProperty(value = "Hotel address")
    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    /**
     * Timezone in the structure containing the name of the city and additional data for the zone
     * itself
     **/
    @ApiModelProperty(required = true, value = "Timezone in the structure containing the name of the city and additional data for the zone itself")
    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Property {\n");

        sb.append("  propertyId: ").append(propertyId).append("\n");
        sb.append("  salesforceId: ").append(salesforceId).append("\n");
        sb.append("  propertyName: ").append(propertyName).append("\n");
        sb.append("  propertyCode: ").append(propertyCode).append("\n");
        sb.append("  website: ").append(website).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  isDemoProperty: ").append(isDemoProperty).append("\n");
        sb.append("  address: ").append(address).append("\n");
        sb.append("  timezone: ").append(timezone).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
