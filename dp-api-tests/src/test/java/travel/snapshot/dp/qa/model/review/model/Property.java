package travel.snapshot.dp.qa.model.review.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class Property  {
  
  private String propertyId = null;

  
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

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Property {\n");
    
    sb.append("  propertyId: ").append(propertyId).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
