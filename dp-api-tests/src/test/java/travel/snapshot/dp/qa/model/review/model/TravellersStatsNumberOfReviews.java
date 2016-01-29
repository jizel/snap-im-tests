package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import travel.snapshot.dp.api.analytics.model.StatsDto;

import java.util.List;

@ApiModel(description = "")
public class TravellersStatsNumberOfReviews extends StatsDto {

    @ApiModelProperty(required = true, value = "")
    @JsonProperty("data")
    private List<TravellerNumberOfReviews> data;

    public TravellersStatsNumberOfReviews() {
    }

    public List<TravellerNumberOfReviews> getData() {
        return this.data;
    }

    public void setData(List<TravellerNumberOfReviews> data) {
        this.data = data;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TravellersStatsNumberOfReviews)) return false;
        final TravellersStatsNumberOfReviews other = (TravellersStatsNumberOfReviews) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$data = this.getData();
        final Object other$data = other.getData();
        if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + super.hashCode();
        final Object $data = this.getData();
        result = result * PRIME + ($data == null ? 0 : $data.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof TravellersStatsNumberOfReviews;
    }

    public String toString() {
        return "travel.snapshot.dp.review.api.model.TravellersStatsNumberOfReviews(super=" + super.toString() + ", data=" + this.getData() + ")";
    }
}
