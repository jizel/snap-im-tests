package travel.snapshot.dp.qa.helpers.review;

/**
 * Following enum has to be in certain casing (lower/upper case) referring to object properties used in review model objects
 * overall is used for travellers
 * overallBubbleRating is used for general analytics data
 */
public enum ReviewAnalyticsEnum {
    ASPECTSOFBUSINESS("aspectsOfBusiness"),
    RATINGSCORE("ratingScore"),
    NUMBEROFREVIEWS("numberOfReviews"),
    OVERALLBUBBLERATING("overallBubbleRating"),
    POPULARITYINDEXRANK("popularityIndexRank"),
    OVERALL("overall");

    private final String name;

    ReviewAnalyticsEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
