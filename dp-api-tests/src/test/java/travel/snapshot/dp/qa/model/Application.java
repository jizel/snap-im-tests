package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class Application {
  
  private String applicationId = null;
  private String applicationName = null;
  private String description = null;
  private String website = null;
  
  /**
   * Unique identifier of the record.
   **/
  @ApiModelProperty(value = "Unique identifier of the record.")
  @JsonProperty("application_id")
  public String getApplicationId() {
      return applicationId;
  }

  public void setApplicationId(String applicationId) {
      this.applicationId = applicationId;
  }

  /**
   * Name of the Application. Must be unique.
   **/
  @ApiModelProperty(value = "Name of the Application. Must be unique.")
  @JsonProperty("application_name")
  public String getApplicationName() {
      return applicationName;
  }

  public void setApplicationName(String applicationName) {
      this.applicationName = applicationName;
  }
  
  /**
   * Short description.
   **/
  @ApiModelProperty(value = "Short description.")
  @JsonProperty("description")
  public String getDescription() {
      return description;
  }

  public void setDescription(String description) {
      this.description = description;
  }
  
  /**
   * Application web site URL.
   **/
  @ApiModelProperty(value = "Application web site URL.")
  @JsonProperty("website")
  public String getWebsite() {
      return website;
  }

  public void setWebsite(String website) {
      this.website = website;
  }
  
  @Override
  public String toString() {
  StringBuilder sb = new StringBuilder();
  sb.append("class Application {\n");
  
  sb.append("  applicationName: ").append(applicationName).append("\n");
  sb.append("  description: ").append(description).append("\n");
  sb.append("  website: ").append(website).append("\n");
  sb.append("  applicationId: ").append(applicationId).append("\n");
  sb.append("}\n");
  return sb.toString();
  }
  
}
