package travel.snapshot.qa.util.container

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.orchestration.Orchestration
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.Properties
import travel.snapshot.qa.util.machine.DockerMachineHelper

class DockerIPLogger {

    private static final Logger logger = LoggerFactory.getLogger(DockerIPLogger)

    /**
     * Logs IPs of Docker containers and Docker machine in case it is running.
     *
     * @param orchestration orchestration to get IPs from
     */
    static def log(final Orchestration orchestration) {

        logger.info("========================================")

        if (Properties.Docker.mode == DockerMode.MACHINE.toString()) {
            def dockerMachineIp = DockerMachineHelper.getIp(Properties.Docker.machineName)
            logger.info("You are running in MACHINE mode, IP address of the Docker machine is {}.", dockerMachineIp)
        }

        orchestration.inspectAllIPs().each { container, ip ->
            logger.info("Container {} has IP {} ", container, ip)
        }

        logger.info("========================================")
    }

}
