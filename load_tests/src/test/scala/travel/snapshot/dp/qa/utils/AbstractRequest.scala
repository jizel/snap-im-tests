package travel.snapshot.dp.qa.utils

import java.util.function.Consumer

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.SessionUtils.EntityType._
import travel.snapshot.dp.qa.utils.SessionUtils._

/**
  * Default get and post requests common for all DP requests
  */
abstract class AbstractRequest extends AbstractSimulation {

  val AUTH_HEADER = "Authorization"

  def getRequest(request: String, additionalQueries: Option[String], url: String, expectedStatus: Int): HttpRequestBuilder = {
    http(request)
      .get(s"$url${additionalQueries.getOrElse("")}")
      .headers(request_headers)
      .header(AUTH_HEADER,  session => setAuthToken(session))
      .check(status.is(expectedStatus))
  }

  def postRequest(request: String, requestBody: String, url: String, expectedStatus: Int): HttpRequestBuilder = {
    http(request).post(url)
      .headers(request_headers)
      .header(AUTH_HEADER,  session => setAuthToken(session))
      .body(StringBody(requestBody))
      .check(status.is(expectedStatus))
  }

  def deleteRequest(requestName: String, url: String, entityType: EntityType, expectedStatus: Int): HttpRequestBuilder = {
    http(requestName).delete(session => s"""$url/${getValue(session, getSessionId(entityType))}""")
      .headers(request_headers)
      .header(AUTH_HEADER,  session => setAuthToken(session))
      .check(status.is(expectedStatus))
  }

  def setAuthToken(session: Session): String = {
    if (SessionUtils.containsValue(session, "accessToken")) s"Bearer ${SessionUtils.getValue(session, "accessToken")}" else s"Bearer $accessTokenParam"
  }

  def getSessionId(entityType: EntityType): String ={
    entityType match {
      case Customer => "customerId"
      case Property => "propertyId"
      case User => "userId"
      case _ => null
    }
  }
}
