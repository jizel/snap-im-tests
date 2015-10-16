package travel.snapshot.dp.qa.model;

import java.util.*;
import java.util.Date;

import io.swagger.annotations.*;
import com.fasterxml.jackson.annotation.JsonProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-08T17:25:39.192+03:00")
public class Stats  {

    private List<Metric> data = new ArrayList<Metric>();
    private Date since = null;
    private Date until = null;
    private String granularity = null;


    /**
     * Collection with the results. Each record in array contains data of one metric - reach, engagement and followers.
     **/
    @ApiModelProperty(required = true, value = "Collection with the results. Each record in array contains data of one metric - reach, engagement and followers.")
    @JsonProperty("data")
    public List<Metric> getData() {
        return data;
    }
    public void setData(List<Metric> data) {
        this.data = data;
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
     * Granularity of result - day, week or month.
     **/
    @ApiModelProperty(required = true, value = "Granularity of result - day, week or month.")
    @JsonProperty("granularity")
    public String getGranularity() {
        return granularity;
    }
    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }



    @Override
    public String toString()  {
        StringBuilder sb = new StringBuilder();
        sb.append("class Stats {\n");

        sb.append("  data: ").append(data).append("\n");
        sb.append("  since: ").append(since).append("\n");
        sb.append("  until: ").append(until).append("\n");
        sb.append("  granularity: ").append(granularity).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}