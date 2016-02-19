package travel.snapshot.qa.util.container

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.PropertyResolver
import travel.snapshot.qa.util.machine.DockerMachineHelper

class DockerIPLogger {

    private static final Logger logger = LoggerFactory.getLogger(DockerIPLogger)

    /**
     * Logs IPs of Docker containers and Docker machine in case it is running.
     *
     * @param orchestration orchestration to get IPs from
     */
    static def log(final DataPlatformOrchestration orchestration) {

        logger.info("========================================")

        if (PropertyResolver.resolveDockerMode() == DockerMode.MACHINE.toString()) {
            def dockerMachineIp = DockerMachineHelper.getIp(PropertyResolver.resolveDockerMachine())
            logger.info("You are running in MACHINE mode, IP address of the Docker machine is {}.", dockerMachineIp)
        }

        orchestration.inspectAllIPs().each { container, ip ->
            logger.info("Container {} has IP {} ", container, ip)
        }

        logger.info("========================================")
    }

}
