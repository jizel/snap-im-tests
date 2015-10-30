package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-27T10:58:41.952+02:00")
public class Property  {
  
  private String propertyId = null;
  private String salesforceId = null;
  private String propertyName = null;
  private String propertyCode = null;
  private String website = null;
  private String email = null;
  private String vatId = null;
  private Boolean isDemoProperty = null;
  private String sourceProperty = null;
  private Address address = null;
  private Address billingAddress = null;
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
   * VAT number (European hotels only)
   **/
  @ApiModelProperty(value = "VAT number (European hotels only)")
  @JsonProperty("vat_id")
  public String getVatId() {
    return vatId;
  }
  public void setVatId(String vatId) {
    this.vatId = vatId;
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
   * ID of the source property if the property is for demo purposes only.
   **/
  @ApiModelProperty(value = "ID of the source property if the property is for demo purposes only.")
  @JsonProperty("source_property")
  public String getSourceProperty() {
    return sourceProperty;
  }
  public void setSourceProperty(String sourceProperty) {
    this.sourceProperty = sourceProperty;
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
   * The billing address of the hotel (in case if it's different than the hotel address)
   **/
  @ApiModelProperty(value = "The billing address of the hotel (in case if it's different than the hotel address)")
  @JsonProperty("billing_address")
  public Address getBillingAddress() {
    return billingAddress;
  }
  public void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }

  
  /**
   * Timezone in the structure containing the name of the city and additional data for the zone itself
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
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Property {\n");
    
    sb.append("  propertyId: ").append(propertyId).append("\n");
    sb.append("  salesforceId: ").append(salesforceId).append("\n");
    sb.append("  propertyName: ").append(propertyName).append("\n");
    sb.append("  propertyCode: ").append(propertyCode).append("\n");
    sb.append("  website: ").append(website).append("\n");
    sb.append("  email: ").append(email).append("\n");
    sb.append("  vatId: ").append(vatId).append("\n");
    sb.append("  isDemoProperty: ").append(isDemoProperty).append("\n");
    sb.append("  sourceProperty: ").append(sourceProperty).append("\n");
    sb.append("  address: ").append(address).append("\n");
    sb.append("  billingAddress: ").append(billingAddress).append("\n");
    sb.append("  timezone: ").append(timezone).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
