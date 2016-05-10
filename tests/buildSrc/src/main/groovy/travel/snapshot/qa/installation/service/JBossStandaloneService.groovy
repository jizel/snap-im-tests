package travel.snapshot.qa.installation.service

import travel.snapshot.qa.docker.Service
import travel.snapshot.qa.docker.manager.DockerServiceManager
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration
import travel.snapshot.qa.manager.jboss.configuration.Management
import travel.snapshot.qa.util.Properties

class JBossStandaloneService {

    static DockerServiceManager<JBossStandaloneManager> init(String containerId) {

        final Service<JBossStandaloneManager, JBossManagerConfiguration> jbossService = new travel.snapshot.qa.manager.jboss.docker.JBossStandaloneService()

        final Management.Builder managementBuilder = new Management.Builder()

        if (Properties.Docker.mode == "MACHINE") {
            managementBuilder.setManagementAddress(Properties.Docker.machineIp)
        }

        final Management management = managementBuilder.build()

        final JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
                .setManagement(management)
                .remote()
                .build()

        jbossService.init(configuration, containerId)
    }
}
