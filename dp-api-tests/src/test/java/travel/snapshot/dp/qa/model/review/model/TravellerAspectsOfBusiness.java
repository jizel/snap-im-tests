package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class TravellerAspectsOfBusiness  {
  
  private String type = null;
  private List<AspectsOfBusiness> aspectsOfBusiness = new ArrayList<AspectsOfBusiness>();

  
  /**
   * Type of traveller.
   **/
  @ApiModelProperty(required = true, value = "Type of traveller.")
  @JsonProperty("type")
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  
  /**
   * Aspects of business by the given type of traveller.
   **/
  @ApiModelProperty(required = true, value = "Aspects of business by the given type of traveller.")
  @JsonProperty("aspects_of_business")
  public List<AspectsOfBusiness> getAspectsOfBusiness() {
    return aspectsOfBusiness;
  }
  public void setAspectsOfBusiness(List<AspectsOfBusiness> aspectsOfBusiness) {
    this.aspectsOfBusiness = aspectsOfBusiness;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TravellerAspectsOfBusiness {\n");
    
    sb.append("  type: ").append(type).append("\n");
    sb.append("  aspectsOfBusiness: ").append(aspectsOfBusiness).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
