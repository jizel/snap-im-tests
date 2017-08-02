package travel.snapshot.dp.qa.utils

import java.util.function.Consumer

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import travel.snapshot.dp.qa.AbstractSimulation

/**
  * Default get and post requests common for all DP requests
  */
abstract class AbstractRequest extends AbstractSimulation {

  def getRequest(request: String, additionalQueries: Option[String], url: String, expectedStatus: Int) = {
    exec(http(request)
      .get(if (additionalQueries.isEmpty) s"$url" else s"$url$additionalQueries")
      .headers(request_headers)
      .header("Authorization",  session => setAuthToken(session))
      .check(status.is(expectedStatus)))
  }

  def postRequest(request: String, requestBody: String, url: String, expectedStatus: Int) = {
    http(request).post(url)
      .headers(request_headers)
      .header("Authorization",  session => setAuthToken(session))
      .body(StringBody(requestBody))
      .check(status.is(expectedStatus))
  }

  def setAuthToken(session: Session): String = {
    if (SessionUtils.containsValue(session, "accessToken")) s"Bearer ${SessionUtils.getValue(session, "accessToken")}" else s"Bearer $accessTokenParam"
  }
}
