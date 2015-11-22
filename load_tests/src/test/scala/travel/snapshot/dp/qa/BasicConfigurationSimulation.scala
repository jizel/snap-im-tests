package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.StringBody

/**
  * Gatling scenario exercising the Configuration module rest API.
  *
  * The basic workflow is to create new configuration type (POST), list configuration types (GET),
  * create 3 new configurations for new configuration type (POST) and list these configurations (GET).
  */
class BasicConfigurationSimulation extends AbstractSimulation("ConfigurationModule-1.0") {

  object CreateConfigurationType {
    def apply() = exec(http("add configuration_type")
        .post("configuration").body(StringBody(session => {
      val configTypeId: String = s""""config_${randomInt(10000000)}""""
      s"""
          {
            "identifier": $configTypeId,
            "description": "description"
          }
        """
    })).asJSON
        .check(status.is(201)).check(header("ETag").exists)
         // save config type ID for later use when creating and getting configurations of given type
        .check(jsonPath("$..identifier").saveAs("configTypeId")))
  }

  object GetConfigurationTypes {
    def apply() = exec(http("get 50 configuration_types")
      .get(s"configuration?limit=${randomInt(50) + 20}&cursor=${randomInt(10) + randomInt(20)}")
      .check(status.is(200)))
  }

  // creates 3 new configurations for each configuration type already created
  object CreateConfigurations {

    def createConfiguration(key: String, value: Any, valueType: String) = {
      exec(http("add new configuration to current configuration type")
        .post("configuration/${configTypeId}").body(StringBody(session =>
        s"""
          {
            "key" : "$key",
            "value" : $value,
            "type" : "$valueType"
          }
        """)).asJSON
        .check(status.is(201)).check(header("ETag").exists))
    }

    def apply() = exec(
      createConfiguration("retryCount", 10, "integer"),
      createConfiguration("retryInterval", 100, "integer"),
      createConfiguration("serviceConfig", """{ "name" : "facebook", "accessToken" : "abc", "valid": 123 }""",
        "object"))
  }

  object GetConfiguration {
    val getConfigurations = exec(http("get configurations for configuration type")
      .get("configuration/${configTypeId}")
      .check(status.is(200))
      // there should be 3 configurations for each new configuration type
      .check(substring("key").count.is(3)))

    val getConfigurationKey = exec(http("get configuration key for configuration type")
      .get("configuration/${configTypeId}/retryCount")
      .check(status.is(200))
      // there should be 3 configurations for each new configuration type
      .check(jsonPath("$..value").ofType[Int].is(10)))

    def apply() = exec(getConfigurations, getConfigurationKey)
  }

  runScenario(
    scenario("Create and get configuration types").exec(
      CreateConfigurationType(), GetConfigurationTypes(), pause(1), CreateConfigurations(), GetConfiguration()))

}
