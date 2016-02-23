package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-19T14:32:40.043+03:00")
public class CustomerProperty {

    private String relationshipId = null;
    private String propertyName = null;
    private String propertyId = null;

    public enum TypeEnum {
        ANCHOR, DATA_OWNER, OWNER, ASSET_MANAGEMENT, MANAGEMENT, CHAINS, MEMBERSHIP,
    }

    ;
    private TypeEnum type = null;
    private Date validFrom = null;
    private Date validTo = null;

    @JsonProperty("relationship_id")
    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    @JsonProperty("property_name")
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Unique identifier of the property.
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the property.")
    @JsonProperty("property_id")
    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }


    /**
     * Type of the relationship. See the ##DOCUMENTATION## for the description.
     **/
    @ApiModelProperty(required = true, value = "Type of the relationship. See the ##DOCUMENTATION## for the description.")
    @JsonProperty("type")
    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }


    /**
     * The date from which the relationship is valid.
     **/
    @ApiModelProperty(required = true, value = "The date from which the relationship is valid.")
    @JsonProperty("valid_from")
    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }


    /**
     * The date to which the relationship is valid.
     **/
    @ApiModelProperty(required = true, value = "The date to which the relationship is valid.")
    @JsonProperty("valid_to")
    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CustomerProperty {\n");

        sb.append("  propertyId: ").append(propertyId).append("\n");
        sb.append("  type: ").append(type).append("\n");
        sb.append("  validFrom: ").append(validFrom).append("\n");
        sb.append("  validTo: ").append(validTo).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
