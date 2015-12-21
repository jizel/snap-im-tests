package travel.snapshot.dp.qa.configuration

import io.gatling.core.Predef._

class ConfigurationSimulation extends AbstractConfigurationSimulation {

  runScenario(
    scenario("Create, get and update configuration types").exec(
      CreateConfigurationType(),
      GetConfigurationTypes(),
      pause(1),
      CreateConfigurations(),
      pause(1),
      UpdateConfigurationDescription(),
      GetConfiguration()))
}
