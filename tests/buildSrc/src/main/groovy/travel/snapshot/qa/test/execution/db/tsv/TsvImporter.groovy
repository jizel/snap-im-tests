package travel.snapshot.qa.test.execution.db.tsv

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration
import travel.snapshot.qa.util.PropertyResolver

class TsvImporter {

    private DataPlatformOrchestration orchestration

    TsvImporter(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration.get()
    }

    def execute() {
        orchestration
                .mariaDBDockerManager
                .serviceManager
                .executeScript PropertyResolver.resolveTsvLoadScript()
    }
}
