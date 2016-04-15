package travel.snapshot.qa.test.execution.db.tsv

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.manager.impl.MariaDBDockerManager
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration

import static travel.snapshot.qa.docker.DockerServiceFactory.MariaDBService.DEFAULT_MARIADB_CONTAINER_ID

class TsvImporter {

    private DataPlatformOrchestration orchestration

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
