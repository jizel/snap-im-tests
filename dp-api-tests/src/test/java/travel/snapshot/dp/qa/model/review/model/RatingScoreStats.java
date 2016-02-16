package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import travel.snapshot.dp.api.analytics.model.StatsDto;

import java.util.List;

@ApiModel(description = "")
public class RatingScoreStats extends StatsDto {

    @ApiModelProperty(required = true, value = "")
    @JsonProperty("values")
    private List<RatingScore> values;

    public RatingScoreStats() {
    }

    public List<RatingScore> getValues() {
        return this.values;
    }

    public void setValues(List<RatingScore> values) {
        this.values = values;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RatingScoreStats)) return false;
        final RatingScoreStats other = (RatingScoreStats) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$values = this.getValues();
        final Object other$values = other.getValues();
        if (this$values == null ? other$values != null : !this$values.equals(other$values)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + super.hashCode();
        final Object $values = this.getValues();
        result = result * PRIME + ($values == null ? 0 : $values.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof RatingScoreStats;
    }

    public String toString() {
        return "travel.snapshot.dp.review.api.model.RatingScoreStats(super=" + super.toString() + ", values=" + this.getValues() + ")";
    }
}