package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-19T14:32:40.043+03:00")
public class PropertySetArray {

    private String propertySetName = null;
    private String propertySetId = null;


    /**
     * Name of the property
     **/
    @ApiModelProperty(required = true, value = "Name of the property")
    @JsonProperty("property_set_name")
    public String getPropertySetName() {
        return propertySetName;
    }

    public void setPropertySetName(String propertySetName) {
        this.propertySetName = propertySetName;
    }


    /**
     * Unique identifier of the property
     **/
    @ApiModelProperty(required = true, value = "Unique identifier of the property")
    @JsonProperty("property_set_id")
    public String getPropertySetId() {
        return propertySetId;
    }

    public void setPropertySetId(String propertySetId) {
        this.propertySetId = propertySetId;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PropertySetArray {\n");

        sb.append("  propertySetName: ").append(propertySetName).append("\n");
        sb.append("  propertySetId: ").append(propertySetId).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
