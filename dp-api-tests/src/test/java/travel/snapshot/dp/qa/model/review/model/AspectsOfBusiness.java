package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class AspectsOfBusiness {

    @ApiModelProperty(value = "Property location.")
    @JsonProperty("location")
    private Double location;

    @ApiModelProperty(value = "Sleep quality.")
    @JsonProperty("sleep_quality")
    private Double sleepQuality;

    @ApiModelProperty(value = "Room.")
    @JsonProperty("room")
    private Double room;

    @ApiModelProperty(value = "Service.")
    @JsonProperty("service")
    private Double service;

    @ApiModelProperty(value = "Value.")
    @JsonProperty("value")
    private Double value;

    @ApiModelProperty(value = "Cleanliness")
    @JsonProperty("cleanliness")
    private Double cleanliness;

    public AspectsOfBusiness() {
    }

    public Double getLocation() {
        return this.location;
    }

    public Double getSleepQuality() {
        return this.sleepQuality;
    }

    public Double getRoom() {
        return this.room;
    }

    public Double getService() {
        return this.service;
    }

    public Double getValue() {
        return this.value;
    }

    public Double getCleanliness() {
        return this.cleanliness;
    }

    public void setLocation(Double location) {
        this.location = location;
    }

    public void setSleepQuality(Double sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public void setRoom(Double room) {
        this.room = room;
    }

    public void setService(Double service) {
        this.service = service;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setCleanliness(Double cleanliness) {
        this.cleanliness = cleanliness;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AspectsOfBusiness)) return false;
        final AspectsOfBusiness other = (AspectsOfBusiness) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$location = this.location;
        final Object other$location = other.location;
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) return false;
        final Object this$sleepQuality = this.sleepQuality;
        final Object other$sleepQuality = other.sleepQuality;
        if (this$sleepQuality == null ? other$sleepQuality != null : !this$sleepQuality.equals(other$sleepQuality))
            return false;
        final Object this$room = this.room;
        final Object other$room = other.room;
        if (this$room == null ? other$room != null : !this$room.equals(other$room)) return false;
        final Object this$service = this.service;
        final Object other$service = other.service;
        if (this$service == null ? other$service != null : !this$service.equals(other$service)) return false;
        final Object this$value = this.value;
        final Object other$value = other.value;
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        final Object this$cleanliness = this.cleanliness;
        final Object other$cleanliness = other.cleanliness;
        if (this$cleanliness == null ? other$cleanliness != null : !this$cleanliness.equals(other$cleanliness))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $location = this.location;
        result = result * PRIME + ($location == null ? 0 : $location.hashCode());
        final Object $sleepQuality = this.sleepQuality;
        result = result * PRIME + ($sleepQuality == null ? 0 : $sleepQuality.hashCode());
        final Object $room = this.room;
        result = result * PRIME + ($room == null ? 0 : $room.hashCode());
        final Object $service = this.service;
        result = result * PRIME + ($service == null ? 0 : $service.hashCode());
        final Object $value = this.value;
        result = result * PRIME + ($value == null ? 0 : $value.hashCode());
        final Object $cleanliness = this.cleanliness;
        result = result * PRIME + ($cleanliness == null ? 0 : $cleanliness.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof AspectsOfBusiness;
    }

    public String toString() {
        return "travel.snapshot.dp.review.api.model.AspectsOfBusiness(location=" + this.location + ", sleepQuality=" + this.sleepQuality + ", room=" + this.room + ", service=" + this.service + ", value=" + this.value + ", cleanliness=" + this.cleanliness + ")";
    }
}
