package travel.snapshot.qa.manager.tomcat.api;

import java.util.stream.Collectors;

/**
 * Filters deployments for given deployment state.
 *
 * @see DeploymentState
 */
public class DeploymentFilter {

    private DeploymentState state;

    public DeploymentFilter(DeploymentState state) {
        this.state = state;
    }

    /**
     * Filters deployments according to some deployment state.
     *
     * @param deployments deployments to filter
     * @return filtered deployments
     */
    public Deployments filter(Deployments deployments) {
        return new Deployments(deployments.getDeployments()
                .stream()
                .filter(deploymentRecord -> deploymentRecord.getDeploymentState() == state)
                .collect(Collectors.toList()));
    }
}
