package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

public class ApplicationVersion {

    private String versionId = null;
    private String apiManagerId = null;
    private String versionName = null;
    private String status = null;
    private String releaseDate = null;
    private String description = null;

    /**
     * Unique identified of the application version.
     **/
    @ApiModelProperty(value = "Unique identified of the application version.")
    @JsonProperty("version_id")
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    /**
     * API manager key.
     **/
    @ApiModelProperty(value = "API manager key.")
    @JsonProperty("api_manager_id")
    public String getApiManagerId() {
        return apiManagerId;
    }

    public void setApiManagerId(String apiManagerId) {
        this.apiManagerId = apiManagerId;
    }

    /**
     * Version name.
     **/
    @ApiModelProperty(value = "Version name.")
    @JsonProperty("version_name")
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * Version status.
     **/
    @ApiModelProperty(value = "Version status.")
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Release date.
     **/
    @ApiModelProperty(value = "Release date.")
    @JsonProperty("release_date")
    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * Description as free text.
     **/
    @ApiModelProperty(value = "Description as free text.")
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ApplicationVersion {\n");

        sb.append("  versionId: ").append(versionId).append("\n");
        sb.append("  apiManagerId: ").append(apiManagerId).append("\n");
        sb.append("  versionName: ").append(versionName).append("\n");
        sb.append("  status: ").append(status).append("\n");
        sb.append("  releaseDate: ").append(releaseDate).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
