package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class Metric  {
  
  private String name = null;
  private List<Integer> values = new ArrayList<Integer>();

  
  /**
   * Name of the collection (metric).
   **/
  @ApiModelProperty(required = true, value = "Name of the collection (metric).")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Array with integer numbers containing data of the given metric.
   **/
  @ApiModelProperty(required = true, value = "Array with integer numbers containing data of the given metric.")
  @JsonProperty("values")
  public List<Integer> getValues() {
    return values;
  }
  public void setValues(List<Integer> values) {
    this.values = values;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metric {\n");
    
    sb.append("  name: ").append(name).append("\n");
    sb.append("  values: ").append(values).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
