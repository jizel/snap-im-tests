package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-05T16:16:51.818+03:00")
public class ConfigurationType {
  
  private String identifier = null;
  private String description = null;

  
  /**
   * Identifier of the configuration type.
   **/
  @ApiModelProperty(required = true, value = "Identifier of the configuration type.")
  @JsonProperty("identifier")
  public String getIdentifier() {
    return identifier;
  }
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  
  /**
   * Describes the configuration type (purpose, values to be stored, key structure).
   **/
  @ApiModelProperty(required = true, value = "Describes the configuration type (purpose, values to be stored, key structure).")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfigurationType {\n");
    
    sb.append("  identifier: ").append(identifier).append("\n");
    sb.append("  description: ").append(description).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
