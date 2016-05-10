package travel.snapshot.qa.installation.service

import travel.snapshot.qa.docker.manager.DockerServiceManager
import travel.snapshot.qa.docker.manager.lifecycle.AfterStartHook
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager
import travel.snapshot.qa.manager.mariadb.impl.docker.MariaDBService

/**
 * There has to be "keycloak" database before Keycloak container is started so Keycloak app server
 * can initialize its tables. When MariaDB container is started, there is not such table so we need to
 * execute post-start hook to create it there for Keycloak app server.
 */
class MariaDBKeycloakService {

    static DockerServiceManager<MariaDBManager> init(String containerId) {

        final DockerServiceManager<MariaDBManager> mariaDBDockerManager = new MariaDBService().init(containerId)

        mariaDBDockerManager.getLifecycleHookExecutor().addAfterStartHook(new AfterStartHook<MariaDBManager>() {
            @Override
            void execute(MariaDBManager manager) {
                manager.executeScript(new StringReader("create database keycloak;"))
            }
        })

        mariaDBDockerManager
    }
}
