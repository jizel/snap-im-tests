package travel.snapshot.dp.qa.tripadvisor

import io.gatling.core.Predef._

class TripAdvisorSimulation extends AbstractTripAdvisorSimulation {

  runScenario(
    scenario("TripAdvisor analytics").exec())
}
