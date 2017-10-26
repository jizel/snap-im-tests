package travel.snapshot.dp.qa.junit.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static travel.snapshot.dp.api.configuration.resources.ConfigurationDefaults.RECORD_PATH;
import static travel.snapshot.dp.api.rateshopper.resources.RateShopperDefaults.MARKET_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.ASPECT_OF_BUSINESS_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.CUSTOMER_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.LOCATIONS_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.LOCATION_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.NUMBER_OF_REVIEWS_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.OVERALL_BUBBLE_RATING_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.POPULARITY_INDEX_RANK_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.PROPERTY_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.RATING_SCORE_PATH;
import static travel.snapshot.dp.api.review.resources.ReviewDefaults.TRAVELLERS_PATH;
import static travel.snapshot.dp.api.socialmedia.resources.SocialMediaDefaults.FACEBOOK_PATH;
import static travel.snapshot.dp.api.socialmedia.resources.SocialMediaDefaults.INSTAGRAM_PATH;
import static travel.snapshot.dp.api.socialmedia.resources.SocialMediaDefaults.TWITTER_PATH;
import static travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults.CONVERSION_RATES;
import static travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults.REFERRALS;
import static travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults.REVENUE;
import static travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults.TOP_VALUES;
import static travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults.VISITS;
import static travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults.VISITS_UNIQUE;

import com.google.common.collect.ImmutableMap;
import jdk.nashorn.internal.objects.annotations.Getter;
import jxl.StringFormulaCell;
import lombok.AllArgsConstructor;
import travel.snapshot.dp.api.configuration.resources.ConfigurationDefaults;
import travel.snapshot.dp.api.rateshopper.resources.RateShopperDefaults;
import travel.snapshot.dp.api.review.resources.ReviewDefaults;
import travel.snapshot.dp.api.socialmedia.resources.FacebookMetric;
import travel.snapshot.dp.api.socialmedia.resources.InstagramMetric;
import travel.snapshot.dp.api.socialmedia.resources.SocialMediaDefaults;
import travel.snapshot.dp.api.webperformance.resources.WebPerformanceDefaults;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;

public class AnalyticsHelpers {

    protected static PropertiesHelper propertiesHelper = new PropertiesHelper();
    private static String PROPERTY_PARAMETER = "testProperty.id";
    private static String CUSTOMER_PARAMETER = "testCustomer.id";
    private static String LOCATION_PARAMETER = "testLocation.id";
    public static String propertyId = propertiesHelper.getProperty(PROPERTY_PARAMETER);
    private static String customerId = propertiesHelper.getProperty(CUSTOMER_PARAMETER);
    private static String locationId = propertiesHelper.getProperty(LOCATION_PARAMETER);
    private static String REVIEW_PATH = ReviewDefaults.BASE_PATH + ReviewDefaults.ANALYTICS_PATH;
    private static String SOCIAL_MEDIA_PATH = SocialMediaDefaults.BASE_PATH + SocialMediaDefaults.ANALYTICS_PATH;
    private static String REACH = "/" + extractName(FacebookMetric.REACH);
    private static String LIKES = "/" + extractName(FacebookMetric.LIKES);
    private static String FOLLOWERS = "/" + extractName(InstagramMetric.FOLLOWERS);
    private static String POSTS = "/posts";
    private static String TWEETS = "/tweets";
    private static String WEB_PERFORMANCE_PATH = WebPerformanceDefaults.BASE_PATH + WebPerformanceDefaults.ANALYTICS_PATH;
    private static String FACEBOOK_PROPERTY_KEY = "/facebook_" + propertyId;
    private static String INSTAGRAM_PROPERTY_KEY = "/instagram_" + propertyId;
    private static String TWITTER_PROPERTY_KEY = "/twitter_" + propertyId;
    private static String TRIP_ADVISOR_PROPERTY_KEY = "/trip_advisor_" + propertyId;
    public static final String RS_MARKET = "rs_market";
    public static final String RS_MARKET_PROPERTIES = "rs_market_properties";
    public static final String RS_PROPERTY = "rs_property";
    private static String RS_PATH = RateShopperDefaults.BASE_PATH + RateShopperDefaults.ANALYTICS_PATH;

    private static String extractName(Enum input) {
        return input.name().toLowerCase();
    }

    public static final List<String> CONFIGURATION_ENDPOINTS = asList(
            ConfigurationDefaults.BASE_PATH,
            ConfigurationDefaults.BASE_PATH + SocialMediaDefaults.BASE_PATH,
            ConfigurationDefaults.BASE_PATH + SocialMediaDefaults.BASE_PATH + RECORD_PATH,
            ConfigurationDefaults.BASE_PATH + WebPerformanceDefaults.BASE_PATH,
            ConfigurationDefaults.BASE_PATH + WebPerformanceDefaults.BASE_PATH + RECORD_PATH,
            ConfigurationDefaults.BASE_PATH + WebPerformanceDefaults.BASE_PATH + RECORD_PATH + "/" + propertyId,
            ConfigurationDefaults.BASE_PATH + ReviewDefaults.BASE_PATH,
            ConfigurationDefaults.BASE_PATH + ReviewDefaults.BASE_PATH + RECORD_PATH,
            ConfigurationDefaults.BASE_PATH + ReviewDefaults.BASE_PATH + RECORD_PATH + TRIP_ADVISOR_PROPERTY_KEY,
            ConfigurationDefaults.BASE_PATH + SocialMediaDefaults.BASE_PATH + RECORD_PATH + FACEBOOK_PROPERTY_KEY,
            ConfigurationDefaults.BASE_PATH + SocialMediaDefaults.BASE_PATH + ConfigurationDefaults.RECORD_PATH + TWITTER_PROPERTY_KEY,
            ConfigurationDefaults.BASE_PATH + SocialMediaDefaults.BASE_PATH + RECORD_PATH + INSTAGRAM_PROPERTY_KEY
    );

    public static final List<String> REVIEW_ENDPOINTS = asList(
            REVIEW_PATH,
            REVIEW_PATH + ASPECT_OF_BUSINESS_PATH,
            REVIEW_PATH + CUSTOMER_PATH + "/" + customerId + ASPECT_OF_BUSINESS_PATH,
            REVIEW_PATH + CUSTOMER_PATH + "/" + customerId + NUMBER_OF_REVIEWS_PATH,
            REVIEW_PATH + CUSTOMER_PATH + "/" + customerId + OVERALL_BUBBLE_RATING_PATH,
            REVIEW_PATH + CUSTOMER_PATH + "/" + customerId + POPULARITY_INDEX_RANK_PATH,
            REVIEW_PATH + NUMBER_OF_REVIEWS_PATH,
            REVIEW_PATH + OVERALL_BUBBLE_RATING_PATH,
            REVIEW_PATH + POPULARITY_INDEX_RANK_PATH,
            REVIEW_PATH + PROPERTY_PATH + "/" + propertyId + ASPECT_OF_BUSINESS_PATH,
            REVIEW_PATH + PROPERTY_PATH + "/" + propertyId + NUMBER_OF_REVIEWS_PATH,
            REVIEW_PATH + PROPERTY_PATH + "/" + propertyId + OVERALL_BUBBLE_RATING_PATH,
            REVIEW_PATH + PROPERTY_PATH + "/" + propertyId + POPULARITY_INDEX_RANK_PATH,
            REVIEW_PATH + RATING_SCORE_PATH,
            REVIEW_PATH + TRAVELLERS_PATH,
            REVIEW_PATH + TRAVELLERS_PATH + ASPECT_OF_BUSINESS_PATH,
            REVIEW_PATH + TRAVELLERS_PATH + NUMBER_OF_REVIEWS_PATH,
            REVIEW_PATH + TRAVELLERS_PATH + OVERALL_BUBBLE_RATING_PATH

    );

    public static final List<String> REVIEW_LOCATIONS_ENDPOINTS = asList(
            ReviewDefaults.BASE_PATH + LOCATION_PATH,
            ReviewDefaults.BASE_PATH + LOCATIONS_PATH,
            ReviewDefaults.BASE_PATH + LOCATIONS_PATH + "/" + locationId + PROPERTIES_PATH

    );

    public static final List<String> SOCIAL_MEDIA_ENDPOINTS = asList(
            SOCIAL_MEDIA_PATH,
            SOCIAL_MEDIA_PATH + REACH,
            SOCIAL_MEDIA_PATH + FACEBOOK_PATH,
            SOCIAL_MEDIA_PATH + FACEBOOK_PATH + REACH,
            SOCIAL_MEDIA_PATH + FACEBOOK_PATH + LIKES,
            SOCIAL_MEDIA_PATH + FACEBOOK_PATH + POSTS,
            SOCIAL_MEDIA_PATH + INSTAGRAM_PATH,
            SOCIAL_MEDIA_PATH + INSTAGRAM_PATH + REACH,
            SOCIAL_MEDIA_PATH + INSTAGRAM_PATH + FOLLOWERS,
            SOCIAL_MEDIA_PATH + TWITTER_PATH,
            SOCIAL_MEDIA_PATH + TWITTER_PATH + REACH,
            SOCIAL_MEDIA_PATH + TWITTER_PATH + FOLLOWERS,
            SOCIAL_MEDIA_PATH + TWITTER_PATH + TWEETS
    );

    public static final List<String> WEB_PERFORMANCE_ENDPOINTS = asList(
            WEB_PERFORMANCE_PATH,
            WEB_PERFORMANCE_PATH + CONVERSION_RATES,
            WEB_PERFORMANCE_PATH + CONVERSION_RATES + WebPerformanceDefaults.COUNTRIES,
            WEB_PERFORMANCE_PATH + REFERRALS,
            WEB_PERFORMANCE_PATH + REFERRALS + WebPerformanceDefaults.CHANNELS,
            WEB_PERFORMANCE_PATH + REVENUE,
            WEB_PERFORMANCE_PATH + TOP_VALUES,
            WEB_PERFORMANCE_PATH + VISITS,
            WEB_PERFORMANCE_PATH + VISITS + WebPerformanceDefaults.COUNTRIES,
            WEB_PERFORMANCE_PATH + VISITS_UNIQUE,
            WEB_PERFORMANCE_PATH + VISITS_UNIQUE + WebPerformanceDefaults.COUNTRIES
            );

    public static final Map<String, String> RATE_SHOPPER_ENDPOINTS = ImmutableMap.of(
            RS_MARKET,            RS_PATH + MARKET_PATH,
            RS_MARKET_PROPERTIES, RS_PATH + MARKET_PATH + RateShopperDefaults.PROPERTIES_PATH,
            RS_PROPERTY,          RS_PATH + PROPERTY_PATH + "/" + propertyId
    );
}
