package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.*;
import travel.snapshot.dp.qa.helpers.StringUtil;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2015-10-02T11:35:15.166+02:00")
public class Customer {

    private String customerId = null;
    private String parentId = null;
    private String salesforceId = null;
    private String commercialSubscriptionId = null;
    private String companyName = null;
    private String code = null;
    private String phone = null;
    private String email = null;
    private String website = null;
    private String vatId = null;
    private Boolean isDemoCustomer = null;
    private String sourceCustomer = null;
    private Address address = null;
    private String notes = null;


    /**
     * Unique identifier of the customer in GUID format.
     **/
    @ApiModelProperty(value = "Unique identifier of the customer in GUID format.")
    @JsonProperty("customer_id")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    /**
     * Unique identifier of the parent customer ID in the GUID format.
     **/
    @ApiModelProperty(value = "Unique identifier of the parent customer ID in the GUID format.")
    @JsonProperty("parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


    /**
     * Salesforce identifier associated with the customer.
     **/
    @ApiModelProperty(value = "Salesforce identifier associated with the customer.")
    @JsonProperty("salesforce_id")
    public String getSalesforceId() {
        return salesforceId;
    }

    public void setSalesforceId(String salesforceId) {
        this.salesforceId = salesforceId;
    }


    /**
     * Unique identifier of the commercial subscription.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the commercial subscription.")
    @JsonProperty("commercial_subscription_id")
    public String getCommercialSubscriptionId() {
        return commercialSubscriptionId;
    }

    public void setCommercialSubscriptionId(String commercialSubscriptionId) {
        this.commercialSubscriptionId = commercialSubscriptionId;
    }


    /**
     * Name of the legal entity. Combination of company name and e-mail must be unique within the system.
     **/
    @ApiModelProperty(required = true, value = "Name of the legal entity. Combination of company name and e-mail must be unique within the system.")
    @JsonProperty("company_name")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    /**
     * Company code.
     **/
    @ApiModelProperty(required = true, value = "Company code.")
    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    /**
     * Official phone number of the customer
     **/
    @ApiModelProperty(value = "Official phone number of the customer")
    @JsonProperty("phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * Official e-mail number of the customer. Combination of company name and e-mail must be unique within the system.
     **/
    @ApiModelProperty(required = true, value = "Official e-mail number of the customer. Combination of company name and e-mail must be unique within the system.")
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Official website of the customer.
     **/
    @ApiModelProperty(value = "Official website of the customer.")
    @JsonProperty("website")
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    /**
     * European VAT number or different official unique identification number.
     **/
    @ApiModelProperty(value = "European VAT number or different official unique identification number.")
    @JsonProperty("vat_id")
    public String getVatId() {
        return vatId;
    }

    public void setVatId(String vatId) {
        this.vatId = vatId;
    }


    /**
     * Indicates whether the customer account is production or for the demo purposes only.
     **/
    @ApiModelProperty(required = true, value = "Indicates whether the customer account is production or for the demo purposes only.")
    @JsonProperty("is_demo_customer")
    public Boolean getIsDemoCustomer() {
        return isDemoCustomer;
    }

    public void setIsDemoCustomer(Boolean isDemoCustomer) {
        this.isDemoCustomer = isDemoCustomer;
    }


    /**
     * Source customer object in case that the customer is for the demo purposes only.
     **/
    @ApiModelProperty(value = "Source customer object in case that the customer is for the demo purposes only.")
    @JsonProperty("source_customer")
    public String getSourceCustomer() {
        return sourceCustomer;
    }

    public void setSourceCustomer(String sourceCustomer) {
        this.sourceCustomer = sourceCustomer;
    }


    /**
     * Billing address
     **/
    @ApiModelProperty(value = "Billing address")
    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    /**
     * Additional notes about customer
     **/
    @ApiModelProperty(value = "Additional notes about customer")
    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Customer {\n");

        sb.append("  customerId: ").append(customerId).append("\n");
        sb.append("  parentId: ").append(parentId).append("\n");
        sb.append("  salesforceId: ").append(salesforceId).append("\n");
        sb.append("  commercialSubscriptionId: ").append(commercialSubscriptionId).append("\n");
        sb.append("  companyName: ").append(companyName).append("\n");
        sb.append("  code: ").append(code).append("\n");
        sb.append("  phone: ").append(phone).append("\n");
        sb.append("  email: ").append(email).append("\n");
        sb.append("  website: ").append(website).append("\n");
        sb.append("  vatId: ").append(vatId).append("\n");
        sb.append("  isDemoCustomer: ").append(isDemoCustomer).append("\n");
        sb.append("  sourceCustomer: ").append(sourceCustomer).append("\n");
        sb.append("  address: ").append(address).append("\n");
        sb.append("  notes: ").append(notes).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
