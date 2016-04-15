package travel.snapshot.qa.test.execution.tomcat

import org.arquillian.spacelift.task.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.DockerServiceFactory
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration

/**
 * Writes out logs from Tomcat.
 */
class TomcatContainerLogger extends Task<DataPlatformOrchestration, Void> {

    private static final Logger logger = LoggerFactory.getLogger(TomcatContainerLogger)

    private static final String DEFAULT_TOMCAT_CONTAINER =
            DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    private final LogOutputStream logOutputStream = new LogOutputStream(logger)

    private String container = DEFAULT_TOMCAT_CONTAINER

    TomcatContainerLogger container(String container) {
        this.container = container
        this
    }

    @Override
    protected Void process(DataPlatformOrchestration orchestration) throws Exception {
        orchestration.getDockerServiceManager(TomcatDockerManager, container)
                .dockerManager
                .clientExecutor
                .copyLog(container, true, true, true, false, 0, logOutputStream)
    }

    LogOutputStream getLogOutputStream() {
        logOutputStream
    }
}
