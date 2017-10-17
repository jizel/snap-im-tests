package travel.snapshot.dp.qa.jms.messages;

import lombok.Getter;

@Getter
public enum Provider {

    SOCIALMEDIA_FACEBOOK("SOCIALMEDIA_FACEBOOK"),
    SOCIALMEDIA_INSTAGRAM("SOCIALMEDIA_INSTAGRAM"),
    SOCIALMEDIA_TWITTER("SOCIALMEDIA_TWITTER"),
    WEBPERFORMANCE_GOOGLEANALYTICS("WEBPERFORMANCE_GOOGLEANALYTICS"),
    REVIEW_TRIPADVISOR("REVIEW_TRIPADVISOR"),
    RATESHOPPING_OTAEXPERT("RATESHOPPING_OTAEXPERT");

    private final String type;

    Provider(String type) {
        this.type = type;
    }

}
