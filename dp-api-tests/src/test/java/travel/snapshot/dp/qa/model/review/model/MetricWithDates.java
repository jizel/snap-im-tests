package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-12-11T15:11:38.265+01:00")
public class MetricWithDates  {
  
  private String name = null;
  private List<Integer> values = new ArrayList<Integer>();
  private Date since = null;
  private Date until = null;
  private String granurality = null;
  private List<String> datasources = new ArrayList<String>();

  
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
   * Integer collection containing the given metric values.
   **/
  @ApiModelProperty(required = true, value = "Integer collection containing the given metric values.")
  @JsonProperty("values")
  public List<Integer> getValues() {
    return values;
  }
  public void setValues(List<Integer> values) {
    this.values = values;
  }

  
  /**
   * Date which specifies the begining of the date period.
   **/
  @ApiModelProperty(required = true, value = "Date which specifies the begining of the date period.")
  @JsonProperty("since")
  public Date getSince() {
    return since;
  }
  public void setSince(Date since) {
    this.since = since;
  }

  
  /**
   * Date which specifies the end of the date period.
   **/
  @ApiModelProperty(required = true, value = "Date which specifies the end of the date period.")
  @JsonProperty("until")
  public Date getUntil() {
    return until;
  }
  public void setUntil(Date until) {
    this.until = until;
  }

  
  /**
   * Granurality of result - day, week or month.
   **/
  @ApiModelProperty(required = true, value = "Granurality of result - day, week or month.")
  @JsonProperty("granurality")
  public String getGranurality() {
    return granurality;
  }
  public void setGranurality(String granurality) {
    this.granurality = granurality;
  }

  
  /**
   * Data sources who provide data to this data series.
   **/
  @ApiModelProperty(required = true, value = "Data sources who provide data to this data series.")
  @JsonProperty("datasources")
  public List<String> getDatasources() {
    return datasources;
  }
  public void setDatasources(List<String> datasources) {
    this.datasources = datasources;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class MetricWithDates {\n");
    
    sb.append("  name: ").append(name).append("\n");
    sb.append("  values: ").append(values).append("\n");
    sb.append("  since: ").append(since).append("\n");
    sb.append("  until: ").append(until).append("\n");
    sb.append("  granurality: ").append(granurality).append("\n");
    sb.append("  datasources: ").append(datasources).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
