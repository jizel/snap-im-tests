package travel.snapshot.dp.qa.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JaxRSServerCodegen", date = "2015-10-08T17:25:39.192+03:00")
public class Metric {

    private String name = null;
    private List<Integer> values = new ArrayList<Integer>();


    /**
     * Collection name.
     **/
    @ApiModelProperty(required = true, value = "Collection name.")
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Array with the values of the given metric.
     **/
    @ApiModelProperty(required = true, value = "Array with the values of the given metric.")
    @JsonProperty("values")
    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Metric {\n");

        sb.append("  name: ").append(name).append("\n");
        sb.append("  values: ").append(values).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
