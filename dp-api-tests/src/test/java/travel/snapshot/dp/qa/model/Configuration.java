package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import cucumber.deps.com.thoughtworks.xstream.annotations.XStreamConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;


@ApiModel(description = "")
@XStreamConverter(NullEmptyStringConverter.class)
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-05T16:16:51.818+03:00")
public class Configuration {

    private String key = null;
    private String value = null;
    private String type = null;


    /**
     * The configuration key.
     **/
    @ApiModelProperty(required = true, value = "The configuration key.")
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    /**
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Type of the value stored.
     **/
    @ApiModelProperty(required = true, value = "Type of the value stored.")
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Configuration {\n");

        sb.append("  key: ").append(key).append("\n");
        sb.append("  value: ").append(value).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
