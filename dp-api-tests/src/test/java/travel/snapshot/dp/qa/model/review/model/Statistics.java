package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import travel.snapshot.dp.api.analytics.model.StatsDto;

import java.util.List;

@ApiModel(description = "")
public class Statistics extends StatsDto {

    /**
     * Ranking of the property in the given geo location.
     *
     */
    @ApiModelProperty(required = true, value = "Ranking of the property in the given geo location.")
    @JsonProperty("popularity_index_rank")
    private List<PopularityIndexRank> popularityIndexRank;

    /**
     * Overall rating of the property.
     *
     */
    @ApiModelProperty(required = true, value = "Overall rating of the property.")
    @JsonProperty("overall_bubble_rating")
    private List<Double> overallBubbleRating;

    /**
     * Total number of reviews of the given property.
     *
     */
    @ApiModelProperty(required = true, value = "Total number of reviews of the given property.")
    @JsonProperty("number_of_reviews")
    private List<Integer> numberOfReviews;

    /**
     * Rating of the hotel broken down by rating (1,2,3,4,5).
     *
     */
    @ApiModelProperty(required = true, value = "Rating of the hotel broken down by rating (1,2,3,4,5).")
    @JsonProperty("rating_score")
    private List<RatingScore> ratingScore;

    /**
     * Rating of the property by different aspects (location, quality of sleep, ...).
     *
     */
    @ApiModelProperty(required = true, value = "Rating of the property by different aspects (location, quality of sleep, ...).")
    @JsonProperty("aspects_of_business")
    private List<AspectsOfBusiness> aspectsOfBusiness;

    public Statistics() {
    }

    public List<PopularityIndexRank> getPopularityIndexRank() {
        return this.popularityIndexRank;
    }

    public List<Double> getOverallBubbleRating() {
        return this.overallBubbleRating;
    }

    public List<Integer> getNumberOfReviews() {
        return this.numberOfReviews;
    }

    public List<RatingScore> getRatingScore() {
        return this.ratingScore;
    }

    public List<AspectsOfBusiness> getAspectsOfBusiness() {
        return this.aspectsOfBusiness;
    }

    public void setPopularityIndexRank(List<PopularityIndexRank> popularityIndexRank) {
        this.popularityIndexRank = popularityIndexRank;
    }

    public void setOverallBubbleRating(List<Double> overallBubbleRating) {
        this.overallBubbleRating = overallBubbleRating;
    }

    public void setNumberOfReviews(List<Integer> numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public void setRatingScore(List<RatingScore> ratingScore) {
        this.ratingScore = ratingScore;
    }

    public void setAspectsOfBusiness(List<AspectsOfBusiness> aspectsOfBusiness) {
        this.aspectsOfBusiness = aspectsOfBusiness;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Statistics)) return false;
        final Statistics other = (Statistics) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$popularityIndexRank = this.getPopularityIndexRank();
        final Object other$popularityIndexRank = other.getPopularityIndexRank();
        if (this$popularityIndexRank == null ? other$popularityIndexRank != null : !this$popularityIndexRank.equals(other$popularityIndexRank))
            return false;
        final Object this$overallBubbleRating = this.getOverallBubbleRating();
        final Object other$overallBubbleRating = other.getOverallBubbleRating();
        if (this$overallBubbleRating == null ? other$overallBubbleRating != null : !this$overallBubbleRating.equals(other$overallBubbleRating))
            return false;
        final Object this$numberOfReviews = this.getNumberOfReviews();
        final Object other$numberOfReviews = other.getNumberOfReviews();
        if (this$numberOfReviews == null ? other$numberOfReviews != null : !this$numberOfReviews.equals(other$numberOfReviews))
            return false;
        final Object this$ratingScore = this.getRatingScore();
        final Object other$ratingScore = other.getRatingScore();
        if (this$ratingScore == null ? other$ratingScore != null : !this$ratingScore.equals(other$ratingScore))
            return false;
        final Object this$aspectsOfBusiness = this.getAspectsOfBusiness();
        final Object other$aspectsOfBusiness = other.getAspectsOfBusiness();
        if (this$aspectsOfBusiness == null ? other$aspectsOfBusiness != null : !this$aspectsOfBusiness.equals(other$aspectsOfBusiness))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + super.hashCode();
        final Object $popularityIndexRank = this.getPopularityIndexRank();
        result = result * PRIME + ($popularityIndexRank == null ? 0 : $popularityIndexRank.hashCode());
        final Object $overallBubbleRating = this.getOverallBubbleRating();
        result = result * PRIME + ($overallBubbleRating == null ? 0 : $overallBubbleRating.hashCode());
        final Object $numberOfReviews = this.getNumberOfReviews();
        result = result * PRIME + ($numberOfReviews == null ? 0 : $numberOfReviews.hashCode());
        final Object $ratingScore = this.getRatingScore();
        result = result * PRIME + ($ratingScore == null ? 0 : $ratingScore.hashCode());
        final Object $aspectsOfBusiness = this.getAspectsOfBusiness();
        result = result * PRIME + ($aspectsOfBusiness == null ? 0 : $aspectsOfBusiness.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Statistics;
    }

    public String toString() {
        return "travel.snapshot.dp.review.api.model.Statistics(super=" + super.toString() + ", popularityIndexRank=" + this.getPopularityIndexRank() + ", overallBubbleRating=" + this.getOverallBubbleRating() + ", numberOfReviews=" + this.getNumberOfReviews() + ", ratingScore=" + this.getRatingScore() + ", aspectsOfBusiness=" + this.getAspectsOfBusiness() + ")";
    }
}
