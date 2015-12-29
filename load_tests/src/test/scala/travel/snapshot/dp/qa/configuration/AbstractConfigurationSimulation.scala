package travel.snapshot.dp.qa.configuration

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.StringBody
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.SessionUtils

/**
  * Encapsulates all steps for some concrete configuration simulation
  */
abstract class AbstractConfigurationSimulation extends AbstractSimulation("ConfigurationModule-1.0") {

  object CreateConfigurationType {
    def apply() = exec(http("add configuration_type")
      .post(session => s"configuration?access_token=$accessToken")
      .body(StringBody(session => {
        val configTypeId: String = s""""config_${randomUtils.randomInt(10000000)}""""
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
      .get(session => s"configuration?access_token=$accessToken&limit=${randomUtils.randomInt(50) + 20}&cursor=${randomUtils.randomInt(10) + randomUtils.randomInt(20)}")
      .check(status.is(200)))
  }

  // creates 3 new configurations for each configuration type already created
  object CreateConfigurations {

    def createConfiguration(key: String, value: Any, valueType: String) = {
      exec(http("add new configuration to current configuration type")
        .post(session => s"configuration/${SessionUtils.getValue(session, "configTypeId")}?access_token=$accessToken")
        .body(StringBody(session =>
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
      createConfiguration("serviceConfig",
        """{"name" : "facebook", "accessToken" : "abc", "valid": 123 }""".stripMargin, "object"))
  }

  object GetConfiguration {
    val getConfigurations = exec(http("get configurations for configuration type")
      .get(session => s"configuration/${SessionUtils.getValue(session, "configTypeId")}?access_token=$accessToken")
      .check(status.is(200))
      // there should be 3 configurations for each new configuration type
      .check(substring("key").count.is(3)))

    val getConfigurationKeyRetryCount = exec(http("get configuration key for configuration type 'retryCount' ")
      .get(session => s"configuration/${SessionUtils.getValue(session, "configTypeId")}/retryCount?access_token=$accessToken")
      .check(status.is(200))
      // there should be 10 configurations for each new configuration type
      .check(jsonPath("$..value").ofType[Int].is(10)))

    val getConfigurationKeyRetryInterval = exec(http("get configuration key for configuration type 'retryInterval' ")
      .get(session => s"configuration/${SessionUtils.getValue(session, "configTypeId")}/retryInterval?access_token=$accessToken")
      .check(status.is(200))
      // there should be 100 configurations for each new configuration type
      .check(jsonPath("$..value").ofType[Int].is(100)))

    def apply() = exec(getConfigurations, getConfigurationKeyRetryCount, getConfigurationKeyRetryInterval)
  }

  object UpdateConfigurationDescription {
    def apply() = exec(http("update configuration description")
      .post(session => s"configuration/${SessionUtils.getValue(session, "configTypeId")}/description_update?access_token=$accessToken")
      .body(StringBody(session =>
        s"""
          {
            "description" : "${randomUtils.randomUUIDAsString}"
          }
        """)).asJSON.check(status.is(204)))
  }

}
