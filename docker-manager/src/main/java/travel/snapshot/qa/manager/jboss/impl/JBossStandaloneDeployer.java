package travel.snapshot.qa.manager.jboss.impl;

import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.standalone.ServerDeploymentHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;
import travel.snapshot.qa.manager.jboss.api.JBossContainerDeployer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Deploys artifacts to a server in a standalone mode.
 */
public class JBossStandaloneDeployer implements JBossContainerDeployer {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneDeployer.class);

    private final ServerDeploymentHelper deployer;

    private final ManagementClient managementClient;

    public JBossStandaloneDeployer(final ManagementClient managementClient) {
        this.managementClient = managementClient;
        deployer = new ServerDeploymentHelper(managementClient.getControllerClient());
    }

    @Override
    public String deploy(String archiveFilePath) throws ContainerDeploymentException {
        return deployInternal(ShrinkWrap.createFromZipFile(WebArchive.class, new File(archiveFilePath)));
    }

    @Override
    public String deploy(Archive<?> archive) throws ContainerDeploymentException {
        return deployInternal(archive);
    }

    @Override
    public void undeploy(String runtimeName) throws ContainerDeploymentException {
        try {
            deployer.undeploy(runtimeName);
            logger.info("Deployment {} has been undeployed.", runtimeName);
        } catch (Exception ex) {
            throw new ContainerDeploymentException(ex);
        }
    }

    // helpers

    private String deployInternal(final Archive<?> archive) throws ContainerDeploymentException {

        checkState();

        final InputStream input = archive.as(ZipExporter.class).exportAsInputStream();

        try {
            return deployInternal(archive.getName(), input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.warn("Failed to close resource {}", input);
                }
            }
        }
    }

    private String deployInternal(final String name, final InputStream input) throws ContainerDeploymentException {

        checkState();

        try {
            String deploymentName = deployer.deploy(name, input);
            logger.info("Deployment {} has been deployed.", deploymentName);
            return deploymentName;
        } catch (Exception ex) {
            Throwable rootCause = ex.getCause();
            while (null != rootCause && rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            throw new ContainerDeploymentException("Cannot deploy: " + name, rootCause);
        }
    }

    private void checkState() {
        if (managementClient != null && managementClient.isClosed()) {
            throw new IllegalStateException("The client connection has been closed.");
        }
    }
}