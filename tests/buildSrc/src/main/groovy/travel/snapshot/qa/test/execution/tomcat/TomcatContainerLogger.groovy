package travel.snapshot.qa.test.execution.tomcat

import org.arquillian.spacelift.task.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.orchestration.Orchestration
import travel.snapshot.qa.manager.tomcat.docker.TomcatDockerManager
import travel.snapshot.qa.manager.tomcat.docker.TomcatService

/**
 * Writes out logs from Tomcat.
 */
class TomcatContainerLogger extends Task<Orchestration, Void> {

    private static final Logger logger = LoggerFactory.getLogger(TomcatContainerLogger)

    private final LogOutputStream logOutputStream = new LogOutputStream(logger)

    private String container = TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    TomcatContainerLogger container(String container) {
        this.container = container
        this
    }

    @Override
    protected Void process(Orchestration orchestration) throws Exception {
        orchestration.getDockerServiceManager(TomcatDockerManager, container)
                .dockerManager
                .clientExecutor
                .copyLog(container, true, true, true, false, 0, logOutputStream)
    }

    LogOutputStream getLogOutputStream() {
        logOutputStream
    }
}
