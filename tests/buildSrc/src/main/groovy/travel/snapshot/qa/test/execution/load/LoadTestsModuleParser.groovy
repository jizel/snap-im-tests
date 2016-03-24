package travel.snapshot.qa.test.execution.load

import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.util.Properties

class LoadTestsModuleParser {

    static List<DataPlatformModule> parse() {
        Properties.LoadTest.simulations
                .collect { LoadTestsSimulation simulation -> simulation.requiredModules() }
                .flatten()
                .unique()
    }
}
