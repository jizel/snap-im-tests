package travel.snapshot.qa.manager.tomcat.api;

import org.jboss.shrinkwrap.api.Archive;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

/**
 * Manages container lifecycle and deployments.
 */
public interface ContainerManager {

    /**
     * Starts a container.
     */
    void start() throws ContainerManagerException;

    /**
     * Stops a container.
     */
    void stop() throws ContainerManagerException;

    /**
     * Checks if container is running.
     *
     * @return true if container is running, false otherwise.
     */
    boolean isRunning() throws ContainerManagerException;

    /**
     * Deploys archive to a container. Archive is represented by its path.
     *
     * @param archiveFilePath path to file with archive to deploy
     * @return TomcatResponse response from container
     */
    TomcatResponse deploy(String archiveFilePath) throws ContainerDeploymentException;

    /**
     * Deploys archive to a container.
     *
     * @param deployment deployment to deploy
     * @return TomcatResponse response from container
     */
    TomcatResponse deploy(Archive<?> deployment) throws ContainerDeploymentException;

    /**
     * Undeploys archive from a container.
     *
     * @param undeployment deployment to undeploy
     * @return TomcatResponse response from container
     */
    TomcatResponse undeploy(Archive<?> undeployment) throws ContainerDeploymentException;

    /**
     * Undeploys archive from a container. Archive is represented by its path.
     *
     * @param archiveFilePath path to file with archive to undeploy
     * @return TomcatResponse response from container
     */
    TomcatResponse undeploy(String archiveFilePath) throws ContainerDeploymentException;

    /**
     * Check if some deployment is deployed and if it is in a running state.
     *
     * @param deployment deployment to check
     * @return true if deployment is deployed and its status is running
     */
    boolean isDeployed(String deployment) throws ContainerManagerException;

    /**
     * Lists all deployments in a container.
     *
     * @return deployments from Tomcat response
     */
    Deployments listDeployments() throws ContainerManagerException;

    /**
     * Filters deployments according to deployment state.
     *
     * @param state state for which the list of deployments will be given
     * @return deployments from TomcatResponse which are in specified state
     */
    Deployments listDeployments(DeploymentState state) throws ContainerManagerException;

    /**
     * Starts a deployment.
     *
     * @param deployment deployment to start
     * @return TomcatResponse response from container
     */
    TomcatResponse startDeployment(String deployment) throws ContainerDeploymentException;

    /**
     * Stops a deployment.
     *
     * @param deployment deployment to stop
     * @return TomcatResponse response from container
     */
    TomcatResponse stopDeployment(String deployment) throws ContainerDeploymentException;

    /**
     * Reloads a deployment.
     *
     * @param deployment deployment to reload
     * @return Tomcat response from container
     */
    TomcatResponse reloadDeployment(String deployment) throws ContainerDeploymentException;

    /**
     * Gets configuration of Tomcat manager
     *
     * @return configuration of this Tomcat manager
     */
    TomcatManagerConfiguration getConfiguration();
}
