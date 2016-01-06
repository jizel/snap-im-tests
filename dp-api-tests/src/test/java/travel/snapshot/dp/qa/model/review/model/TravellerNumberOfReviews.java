package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class TravellerNumberOfReviews  {
  
  private String type = null;
  private List<Integer> numberOfReviews = new ArrayList<Integer>();

  
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
   * Total number of reviews by the given type of traveller.
   **/
  @ApiModelProperty(required = true, value = "Total number of reviews by the given type of traveller.")
  @JsonProperty("number_of_reviews")
  public List<Integer> getNumberOfReviews() {
    return numberOfReviews;
  }
  public void setNumberOfReviews(List<Integer> numberOfReviews) {
    this.numberOfReviews = numberOfReviews;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TravellerNumberOfReviews {\n");
    
    sb.append("  type: ").append(type).append("\n");
    sb.append("  numberOfReviews: ").append(numberOfReviews).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
