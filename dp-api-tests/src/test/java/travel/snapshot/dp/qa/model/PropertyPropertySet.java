package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-30T09:19:32.037+02:00")
public class PropertyPropertySet {

    private String propertyId = null;
    private String propertyName = null;

    @JsonProperty("property_name")
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    /**
     * Unique identifier of the property
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the property")
    @JsonProperty("property_id")
    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PropertyPropertySet {\n");

        sb.append("  propertyId: ").append(propertyId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
