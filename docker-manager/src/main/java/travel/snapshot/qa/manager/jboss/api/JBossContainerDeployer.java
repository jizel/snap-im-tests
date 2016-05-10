package travel.snapshot.qa.manager.jboss.api;

import org.jboss.shrinkwrap.api.Archive;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;

import java.io.File;

public interface JBossContainerDeployer {

    /**
     * Deploys archive to a container. Archive is represented by its path.
     *
     * @param archiveFilePath path to file with archive to deploy
     * @return runtime deployment name
     */
    String deploy(String archiveFilePath) throws ContainerDeploymentException;

    /**
     * Deploys archive to a container. Archive is represented by its path.
     *
     * @param archive file witch archive to deploy
     * @return runtime deployment name
     */
    String deploy(File archive) throws ContainerDeploymentException;

    /**
     * Deploys archive to a container.
     *
     * @param deployment deployment to deploy
     * @return runtime deployment name
     */
    String deploy(Archive<?> deployment) throws ContainerDeploymentException;

    /**
     * Undeploys archive from a container. Archive is represented by its path.
     *
     * @param runtimeName runtime name of archive to undeploy
     */
    void undeploy(String runtimeName) throws ContainerDeploymentException;
}
