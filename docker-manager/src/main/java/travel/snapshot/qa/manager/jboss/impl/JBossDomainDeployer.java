package travel.snapshot.qa.manager.jboss.impl;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.as.controller.client.helpers.domain.DomainClient;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;
import travel.snapshot.qa.manager.jboss.api.JBossContainerDeployer;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class JBossDomainDeployer implements JBossContainerDeployer {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneDeployer.class);

    private final ArchiveDeployer deployer;

    private final JBossManagerConfiguration configuration;

    private Set<String> serverGroups = new HashSet<>();

    public JBossDomainDeployer(final DomainClient domainClient, final JBossManagerConfiguration configuration) {
        deployer = new ArchiveDeployer(domainClient.getDeploymentManager());
        this.configuration = configuration;
    }

    public JBossDomainDeployer serverGroup(String serverGroup) {
        this.serverGroups.clear();
        this.serverGroups.add(serverGroup);
        return this;
    }

    public JBossDomainDeployer serverGroups(Set<String> serverGroups) {
        this.serverGroups.clear();
        this.serverGroups.addAll(serverGroups);
        return this;
    }

    @Override
    public String deploy(String archiveFilePath) throws ContainerDeploymentException {
        return deploy(ShrinkWrap.createFromZipFile(WebArchive.class, new File(archiveFilePath)));
    }

    @Override
    public String deploy(Archive<?> archive) throws ContainerDeploymentException {
        try {

            String runtimeName = deployer.deploy(archive, getServerGroups(configuration));
            logger.info("Deployment {} has been deployed.", runtimeName);
            return runtimeName;
        } catch (DeploymentException ex) {
            throw new ContainerDeploymentException(ex);
        }
    }

    @Override
    public void undeploy(String runtimeName) throws ContainerDeploymentException {
        try {
            deployer.undeploy(runtimeName, getServerGroups(configuration));
            logger.info("Deployment {} has been undeployed.", runtimeName);
        } catch (DeploymentException ex) {
            throw new ContainerDeploymentException(ex);
        }
    }

    private Set<String> getServerGroups(JBossManagerConfiguration configuration) {

        if (this.serverGroups.isEmpty()) {
            this.serverGroups.add(configuration.getConfiguration().getServerGroup());
        }

        return this.serverGroups;
    }
}
