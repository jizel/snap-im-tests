package travel.snapshot.dp.qa.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import travel.snapshot.dp.qa.helpers.StringUtil;


@ApiModel(description = "")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2015-10-02T11:35:15.166+02:00")
public class Address {

    private String addressLine1 = null;
    private String addressLine2 = null;
    private String city = null;
    private String zipCode = null;
    private String country = null;


    /**
     * Main address line
     **/
    @ApiModelProperty(required = true, value = "Main address line")
    @JsonProperty("address_line1")
    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }


    /**
     * Additional adress line
     **/
    @ApiModelProperty(value = "Additional adress line")
    @JsonProperty("address_line2")
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }


    /**
     * City
     **/
    @ApiModelProperty(required = true, value = "City")
    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    /**
     * ZipCode
     **/
    @ApiModelProperty(required = true, value = "ZipCode")
    @JsonProperty("zip_code")
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }


    /**
     * Country
     **/
    @ApiModelProperty(required = true, value = "Country")
    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Address {\n");

        sb.append("    addressLine1: ").append(StringUtil.toIndentedString(addressLine1)).append("\n");
        sb.append("    addressLine2: ").append(StringUtil.toIndentedString(addressLine2)).append("\n");
        sb.append("    city: ").append(StringUtil.toIndentedString(city)).append("\n");
        sb.append("    zipCode: ").append(StringUtil.toIndentedString(zipCode)).append("\n");
        sb.append("    country: ").append(StringUtil.toIndentedString(country)).append("\n");
        sb.append("}");
        return sb.toString();
    }
}
