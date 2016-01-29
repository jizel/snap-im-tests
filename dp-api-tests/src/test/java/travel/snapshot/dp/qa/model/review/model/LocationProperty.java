package travel.snapshot.dp.qa.model.review.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class LocationProperty  {
  
  private String locationId = null;

  
  /**
   * Identifier of the geographical location of the property according to data provider (city district, city or region).
   **/
  @ApiModelProperty(required = true, value = "Identifier of the geographical location of the property according to data provider (city district, city or region).")
  @JsonProperty("location_id")
  public String getLocationId() {
    return locationId;
  }
  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocationProperty {\n");
    
    sb.append("  locationId: ").append(locationId).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
