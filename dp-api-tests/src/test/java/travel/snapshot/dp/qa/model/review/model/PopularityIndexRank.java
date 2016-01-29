package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class PopularityIndexRank {

    @ApiModelProperty(required = true, value = "Rank of the hotel in the givem location.")
    @JsonProperty("ranking")
    private Integer ranking;

    @ApiModelProperty(required = true, value = "Total number of hotels in the given location.")
    @JsonProperty("total_number")
    private Integer totalNumber;

    public PopularityIndexRank() {
    }

    public Integer getRanking() {
        return this.ranking;
    }

    public Integer getTotalNumber() {
        return this.totalNumber;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof PopularityIndexRank)) return false;
        final PopularityIndexRank other = (PopularityIndexRank) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$ranking = this.ranking;
        final Object other$ranking = other.ranking;
        if (this$ranking == null ? other$ranking != null : !this$ranking.equals(other$ranking)) return false;
        final Object this$totalNumber = this.totalNumber;
        final Object other$totalNumber = other.totalNumber;
        if (this$totalNumber == null ? other$totalNumber != null : !this$totalNumber.equals(other$totalNumber))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $ranking = this.ranking;
        result = result * PRIME + ($ranking == null ? 0 : $ranking.hashCode());
        final Object $totalNumber = this.totalNumber;
        result = result * PRIME + ($totalNumber == null ? 0 : $totalNumber.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof PopularityIndexRank;
    }

    public String toString() {
        return "travel.snapshot.dp.review.api.model.PopularityIndexRank(ranking=" + this.ranking + ", totalNumber=" + this.totalNumber + ")";
    }
}
