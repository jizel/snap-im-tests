package travel.snapshot.qa.manager.jboss.impl;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.as.controller.client.helpers.domain.DeploymentPlan;
import org.jboss.as.controller.client.helpers.domain.DeploymentPlanBuilder;
import org.jboss.as.controller.client.helpers.domain.DeploymentPlanResult;
import org.jboss.as.controller.client.helpers.domain.DomainDeploymentManager;
import org.jboss.as.controller.client.helpers.domain.RemoveDeploymentPlanBuilder;
import org.jboss.as.controller.client.helpers.domain.ServerGroupDeploymentPlanBuilder;
import org.jboss.as.controller.client.helpers.domain.UndeployDeploymentPlanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Future;

/**
 * This was taken from upstream ArchiveDeployer because it does not remove deployments, just deletes them and they
 * are left "unassigned" so it is not possible to do redeploy.
 */
public class ArchiveDeployer extends org.jboss.as.arquillian.container.domain.ArchiveDeployer {

    private static final Logger logger = LoggerFactory.getLogger(ArchiveDeployer.class);

    private final DomainDeploymentManager deploymentManager;

    public ArchiveDeployer(DomainDeploymentManager deploymentManager) {
        super(deploymentManager);
        this.deploymentManager = deploymentManager;
    }

    @Override
    public void undeploy(String runtimeName, Set<String> serverGroups) throws DeploymentException {
        if (serverGroups.isEmpty()) {
            throw new DeploymentException("No target server groups to deploy to.");
        }
        try {
            DeploymentPlanBuilder builder = deploymentManager.newDeploymentPlan();
            RemoveDeploymentPlanBuilder removeBuilder = builder.remove(runtimeName);
            UndeployDeploymentPlanBuilder undeployBuilder = builder.undeploy(runtimeName);
            ServerGroupDeploymentPlanBuilder serverGroupBuilder = null;
            ServerGroupDeploymentPlanBuilder serverGroupRemoveBuilder = null;
            for (String target : serverGroups) {
                serverGroupBuilder = (serverGroupBuilder == null ? undeployBuilder.toServerGroup(target) : serverGroupBuilder.toServerGroup(target));
            }

            for (String target : serverGroups) {
                serverGroupRemoveBuilder = (serverGroupRemoveBuilder == null ? removeBuilder.toServerGroup(target) : serverGroupRemoveBuilder.toServerGroup(target));
            }

            DeploymentPlan plan = serverGroupBuilder.build();
            Future<DeploymentPlanResult> future = deploymentManager.execute(plan);
            future.get();

            DeploymentPlan removePlan = serverGroupRemoveBuilder.build();
            Future<DeploymentPlanResult> removeFuture = deploymentManager.execute(removePlan);
            removeFuture.get();

        } catch (Exception ex) {
            logger.warn("Cannot undeploy: " + runtimeName + ":" + ex.getMessage());
        }
    }
}
