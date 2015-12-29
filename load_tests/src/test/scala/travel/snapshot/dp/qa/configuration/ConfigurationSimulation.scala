package travel.snapshot.dp.qa.configuration

import io.gatling.core.Predef._

class ConfigurationSimulation extends AbstractConfigurationSimulation {

  runScenario(
    scenario("Create, get and update configuration types").exec(
      CreateConfigurationType(),
      pause(1),
      GetConfigurationTypes(),
      pause(1),
      CreateConfigurations(),
      pause(1),
      UpdateConfigurationDescription(),
      pause(1),
      GetConfiguration()))
}
