package travel.snapshot.dp.qa.model.review.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class Location  {
  
  private String locationId = null;
  private String locationName = null;

  
  /**
   * Unique identifier of the location.
   **/
  @ApiModelProperty(required = true, value = "Unique identifier of the location.")
  @JsonProperty("location_id")
  public String getLocationId() {
    return locationId;
  }
  public void setLocationId(String locationId) {
    this.locationId = locationId;
  }

  
  /**
   * Location name.
   **/
  @ApiModelProperty(required = true, value = "Location name.")
  @JsonProperty("location_name")
  public String getLocationName() {
    return locationName;
  }
  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Location {\n");
    
    sb.append("  locationId: ").append(locationId).append("\n");
    sb.append("  locationName: ").append(locationName).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
