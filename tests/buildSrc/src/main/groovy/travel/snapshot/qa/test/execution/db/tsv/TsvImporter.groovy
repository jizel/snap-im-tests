package travel.snapshot.qa.test.execution.db.tsv

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.orchestration.Orchestration
import travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBDockerManager

import static travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID

class TsvImporter {

    private Orchestration orchestration

    private String container = DEFAULT_MARIADB_CONTAINER_ID

    TsvImporter(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration.get()
    }

    TsvImporter container(String container) {
        this.container = container
        this
    }

    def execute() {
        orchestration.getDockerServiceManager(MariaDBDockerManager, container)
                .serviceManager
                .executeScript TsvLoadScriptResolver.resolve()
    }
}
