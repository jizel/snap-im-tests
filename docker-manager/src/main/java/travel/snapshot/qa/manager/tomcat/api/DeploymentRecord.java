package travel.snapshot.qa.manager.tomcat.api;

import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents single deployment line in a listing result
 */
public class DeploymentRecord {

    private String contextPath;

    private DeploymentState deploymentState;

    private int activeSessions;

    private DeploymentRecord(String contextPath, DeploymentState deploymentState, int activeSessions) {
        this.contextPath = contextPath;
        this.deploymentState = deploymentState;
        this.activeSessions = activeSessions;
    }

    public String getContextPath() {
        return contextPath;
    }

    public DeploymentState getDeploymentState() {
        return deploymentState;
    }

    public int getActiveSessions() {
        return activeSessions;
    }

    public boolean isRunning() {
        return deploymentState == DeploymentState.RUNNING;
    }

    public static class DeploymentRecordsBuilder {

        private static final Pattern deploymentLinePattern = Pattern.compile("(.*):(.*):(.*):(.*)");

        private final List<String> deploymentLines;

        /**
         * @param responseBody Tomcat response body after list command
         * @throws IllegalArgumentException if {@code responseBody} is a null object
         */
        public DeploymentRecordsBuilder(final TomcatResponseBody responseBody) {
            if (responseBody == null) {
                throw new IllegalArgumentException("Tomcat response body must not be a null object.");
            }

            this.deploymentLines = responseBody.getBody();
        }

        /**
         * Builds a list of {@link DeploymentRecord} from a {@link TomcatResponseBody}.
         *
         * @return list of deployment records parsed from {@link TomcatResponseBody}
         * @throws DeploymentRecordsBuilderException in case some line in {@link TomcatResponseBody} is not parseable to
         *                                           {@link DeploymentRecord}.
         */
        public List<DeploymentRecord> build() throws DeploymentRecordsBuilderException {
            List<DeploymentRecord> deploymentRecords = new ArrayList<>();

            for (String deploymentLine : deploymentLines) {
                deploymentRecords.add(parse(deploymentLine));

            }

            return deploymentRecords;
        }

        private DeploymentRecord parse(String deploymentLine) throws DeploymentRecordsBuilderException {
            Matcher deploymentMatcher = deploymentLinePattern.matcher(deploymentLine);

            if (!deploymentMatcher.matches()) {
                throw new DeploymentRecordsBuilderException(
                        String.format("Deployment line '%s' does not match the matcher.", deploymentLine));
            }

            try {
                String context = deploymentMatcher.group(1);
                DeploymentState deploymentState = DeploymentState.parse(deploymentMatcher.group(2));
                int sessions = Integer.valueOf(deploymentMatcher.group(3));

                return new DeploymentRecord(context, deploymentState, sessions);
            } catch (NumberFormatException | DeploymentStateParserException ex) {
                throw new DeploymentRecordsBuilderException(
                        String.format("Unable to parse deployment record from deployment line '%s'", deploymentLine),
                        ex);
            }
        }
    }
}
