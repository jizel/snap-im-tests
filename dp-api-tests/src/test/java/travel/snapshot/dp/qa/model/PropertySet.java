package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-30T09:19:32.037+02:00")
public class PropertySet {

    private String propertySetId = null;
    private String propertySetName = null;
    private String propertySetDescription = null;
    private String customerId = null;
    private String parentId = null;
    private String propertySetType = null;
    private Integer isActive = null;


    /**
     * Unique identifier of the property set in GUID format.
     **/
    @ApiModelProperty(value = "Unique identifier of the property set in GUID format.")
    @JsonProperty("property_set_id")
    public String getPropertySetId() {
        return propertySetId;
    }

    public void setPropertySetId(String propertySetId) {
        this.propertySetId = propertySetId;
    }


    /**
     * Name of the property set.
     **/
    @ApiModelProperty(required = true, value = "Name of the property set.")
    @JsonProperty("property_set_name")
    public String getPropertySetName() {
        return propertySetName;
    }

    public void setPropertySetName(String propertySetName) {
        this.propertySetName = propertySetName;
    }


    /**
     * Description of the property set.
     **/
    @ApiModelProperty(value = "Description of the property set.")
    @JsonProperty("property_set_description")
    public String getPropertySetDescription() {
        return propertySetDescription;
    }

    public void setPropertySetDescription(String propertySetDescription) {
        this.propertySetDescription = propertySetDescription;
    }


    /**
     * Unique identifier of the parent tenant in GUID format.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the parent tenant in GUID format.")
    @JsonProperty("customer_id")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }


    /**
     * Identifier of the parent property set (simple hiearchical structure is allowed).
     **/
    @ApiModelProperty(value = "Identifier of the parent property set (simple hiearchical structure is allowed).")
    @JsonProperty("parent_id")
    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }


    /**
     * Type of property set e.g. Brand or Chain
     **/
    @ApiModelProperty(required = true, value = "Type of property set e.g. Brand or Chain")
    @JsonProperty("property_set_type")
    public String getPropertySetType() {
        return propertySetType;
    }

    public void setPropertySetType(String propertySetType) {
        this.propertySetType = propertySetType;
    }


    /**
     * Indicates whether the property is active
     **/
    @ApiModelProperty(value = "Indicates whether the property is active")
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
        sb.append("class PropertySet {\n");

        sb.append("  propertySetId: ").append(propertySetId).append("\n");
        sb.append("  propertySetName: ").append(propertySetName).append("\n");
        sb.append("  propertySetDescription: ").append(propertySetDescription).append("\n");
        sb.append("  customerId: ").append(customerId).append("\n");
        sb.append("  parentId: ").append(parentId).append("\n");
        sb.append("  propertySetType: ").append(propertySetType).append("\n");
        sb.append("  isActive: ").append(isActive).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
