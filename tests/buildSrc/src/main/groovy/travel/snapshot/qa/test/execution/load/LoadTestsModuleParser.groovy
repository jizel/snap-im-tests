package travel.snapshot.qa.test.execution.load

import travel.snapshot.qa.test.execution.dataplatform.DataPlatformModule
import travel.snapshot.qa.util.PropertyResolver

class LoadTestsModuleParser {

    static List<DataPlatformModule> parse() {
        PropertyResolver.resolveLoadTestSimulations()
                .collect { LoadTestsSimulation simulation -> simulation.requiredModules() }
                .flatten()
                .unique()
    }
}
