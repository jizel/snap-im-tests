package travel.snapshot.qa.test.execution.load

class OAuthConfiguration {

    String oauthClientId

    String oauthClientSecret

    OAuthConfiguration(String oauthClientId, String oauthClientSecret) {
        this.oauthClientId = oauthClientId
        this.oauthClientSecret = oauthClientSecret
    }
}
