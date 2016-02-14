package travel.snapshot.qa.test.execution.tomcat

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.DockerServiceFactory
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.PropertyResolver
import travel.snapshot.qa.util.interaction.DockerInteraction

/**
 * Copies Tomcat Spring configuration to virtual machine where it will be mounted to respective Tomcat container.
 */
class TomcatConfigurationDeployer {

    private static final Logger logger = LoggerFactory.getLogger(TomcatConfigurationDeployer)

    private String dockerMachine

    private String containerId = DockerServiceFactory.TomcatService.DEFAULT_TOMCAT_CONTAINER_ID

    TomcatConfigurationDeployer(String dockerMachine) {
        this.dockerMachine = dockerMachine
    }

    TomcatConfigurationDeployer container(String containerId) {
        this.containerId = containerId
        this
    }

    def deploy() {

        if (PropertyResolver.resolveDockerMode() != DockerMode.MACHINE.name()) {
            return
        }

        String tomcatConfigurationDir = new GradleSpaceliftDelegate().project().spacelift.configuration['tomcatSpringConfigDirectory'].value

        String from = tomcatConfigurationDir + "/${containerId}"
        String to = dockerMachine + ":/home/docker/configuration"

        logger.info("Copying Tomcat Spring configuration directory from {} to {}.", from, to)

        DockerInteraction.execute("sudo -i chown -R docker:staff /home/docker/configuration/${containerId}", 0, 1)
        DockerInteraction.copy(from, to, true)

        DockerInteraction.execute("ls -la /home/docker /home/docker/configuration /home/docker/configuration/${containerId}")
    }
}
