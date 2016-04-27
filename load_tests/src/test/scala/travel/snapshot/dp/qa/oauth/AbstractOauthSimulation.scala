package travel.snapshot.dp.qa.oauth

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation

abstract class AbstractOauthSimulation extends AbstractSimulation {

  /**
    * Needs only
    * - grant_type = client_credentials
    * - client_id - e.g TestClient - you find this in Keycloak backend
    * - client_secret - e.g. 9b28e628-20b7-4a2c-9607-79f43d35069c - you find this in Keycloak backend
    */
  object CreateOauthTokenWithoutUserContext {
    def apply() = exec(http("create oauth token")
      .post(session => s"token")
      .asFormUrlEncoded
      .body(StringBody(_ => s"""grant_type=client_credentials&client_id=${oauthClientId}&client_secret=${oauthClientSecret}"""))
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("oauthAccessToken"))
      .check(jsonPath("$..refresh_token").saveAs("oauthRefreshToken"))
    )
  }
}
