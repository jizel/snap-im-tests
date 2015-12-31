package travel.snapshot.qa.manager.tomcat.api;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates information about deployments of a container
 */
public class Deployments {

    private final List<DeploymentRecord> deploymentRecords;

    public Deployments(List<DeploymentRecord> deploymentRecords) {
        this.deploymentRecords = deploymentRecords;
    }

    public List<DeploymentRecord> getDeployments() {
        return Collections.unmodifiableList(deploymentRecords);
    }

    /**
     * Gets deployment record according to its context.
     *
     * @param context context of deployment
     * @return deployment record with given context or null if not found
     */
    public DeploymentRecord getDeployment(String context) {

        final String normalizedContext = normalize(context);

        return deploymentRecords.stream()
                .filter(deploymentRecord -> normalizedContext.equals(deploymentRecord.getContextPath()))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return number of deployments
     */
    public int size() {
        return deploymentRecords.size();
    }

    /**
     * Checks if some deployment is present.
     *
     * @param deployment deployment to check
     * @return true if deployment is deployed, false otherwise
     */
    public boolean contains(String deployment) {
        return getDeployment(deployment) != null;
    }

    private String normalize(String context) {
        if (!context.startsWith("/")) {
            context = "/" + context;
        }

        return context;
    }
}
