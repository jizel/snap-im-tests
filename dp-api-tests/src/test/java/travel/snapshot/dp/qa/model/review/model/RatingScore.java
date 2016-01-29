package travel.snapshot.dp.qa.model.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class RatingScore {

    @ApiModelProperty(required = true, value = "Number of rankings as 1 (lowest).")
    @JsonProperty("One")
    private Integer one;

    @ApiModelProperty(required = true, value = "Number of rankings as 2.")
    @JsonProperty("Two")
    private Integer two;

    @ApiModelProperty(required = true, value = "Number of rankings as 3.")
    @JsonProperty("Three")
    private Integer three;

    @ApiModelProperty(required = true, value = "Number of rankings as 4.")
    @JsonProperty("Four")
    private Integer four;

    @ApiModelProperty(required = true, value = "Number of rankings as 5.")
    @JsonProperty("Five")
    private Integer five;

    public RatingScore() {
    }

    public Integer getOne() {
        return this.one;
    }

    public Integer getTwo() {
        return this.two;
    }

    public Integer getThree() {
        return this.three;
    }

    public Integer getFour() {
        return this.four;
    }

    public Integer getFive() {
        return this.five;
    }

    public void setOne(Integer one) {
        this.one = one;
    }

    public void setTwo(Integer two) {
        this.two = two;
    }

    public void setThree(Integer three) {
        this.three = three;
    }

    public void setFour(Integer four) {
        this.four = four;
    }

    public void setFive(Integer five) {
        this.five = five;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof RatingScore)) return false;
        final RatingScore other = (RatingScore) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$one = this.one;
        final Object other$one = other.one;
        if (this$one == null ? other$one != null : !this$one.equals(other$one)) return false;
        final Object this$two = this.two;
        final Object other$two = other.two;
        if (this$two == null ? other$two != null : !this$two.equals(other$two)) return false;
        final Object this$three = this.three;
        final Object other$three = other.three;
        if (this$three == null ? other$three != null : !this$three.equals(other$three)) return false;
        final Object this$four = this.four;
        final Object other$four = other.four;
        if (this$four == null ? other$four != null : !this$four.equals(other$four)) return false;
        final Object this$five = this.five;
        final Object other$five = other.five;
        if (this$five == null ? other$five != null : !this$five.equals(other$five)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $one = this.one;
        result = result * PRIME + ($one == null ? 0 : $one.hashCode());
        final Object $two = this.two;
        result = result * PRIME + ($two == null ? 0 : $two.hashCode());
        final Object $three = this.three;
        result = result * PRIME + ($three == null ? 0 : $three.hashCode());
        final Object $four = this.four;
        result = result * PRIME + ($four == null ? 0 : $four.hashCode());
        final Object $five = this.five;
        result = result * PRIME + ($five == null ? 0 : $five.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof RatingScore;
    }

    public String toString() {
        return "travel.snapshot.dp.review.api.model.RatingScore(one=" + this.one + ", two=" + this.two + ", three=" + this.three + ", four=" + this.four + ", five=" + this.five + ")";
    }
}
