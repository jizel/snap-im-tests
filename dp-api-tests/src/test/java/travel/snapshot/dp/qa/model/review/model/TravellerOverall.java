package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class TravellerOverall  {
  
  private String type = null;
  private List<Double> overall = new ArrayList<Double>();

  
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
   * Overall rating by the given type of traveller.
   **/
  @ApiModelProperty(required = true, value = "Overall rating by the given type of traveller.")
  @JsonProperty("overall")
  public List<Double> getOverall() {
    return overall;
  }
  public void setOverall(List<Double> overall) {
    this.overall = overall;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class TravellerOverall {\n");
    
    sb.append("  type: ").append(type).append("\n");
    sb.append("  overall: ").append(overall).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
