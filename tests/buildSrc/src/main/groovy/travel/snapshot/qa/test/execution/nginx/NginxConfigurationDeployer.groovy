package travel.snapshot.qa.test.execution.nginx

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.Properties
import travel.snapshot.qa.util.interaction.DockerInteraction

class NginxConfigurationDeployer {

    private static final Logger logger = LoggerFactory.getLogger(NginxConfigurationDeployer)

    private String dockerMachine

    private String containerId = "nginx"

    NginxConfigurationDeployer(String dockerMachine) {
        this.dockerMachine = dockerMachine
    }

    NginxConfigurationDeployer container(String containerId) {
        this.containerId = containerId
        this
    }

    def deploy() {

        if (Properties.Docker.mode != DockerMode.MACHINE.name()) {
            logger.info("Docker mode is not set to MACHINE, skipping")
            return
        }

        String from = Properties.Nginx.nginxConfigDirectorySource
        String to = dockerMachine + ":/home/docker/configuration"

        logger.info("Copying Nginx configuration directory from {} to {}.", from, to)

        DockerInteraction.execute("sudo -i chown -R docker:staff /home/docker/configuration/${containerId}", 0, 1)
        DockerInteraction.copy(from, to, true)

        DockerInteraction.execute("ls -la /home/docker /home/docker/configuration /home/docker/configuration/${containerId}")
    }
}
