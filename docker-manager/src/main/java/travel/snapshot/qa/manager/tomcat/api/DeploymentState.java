package travel.snapshot.qa.manager.tomcat.api;

/**
 * Represents enumeration of states in which some deployment in a Tomcat container can be.
 */
public enum DeploymentState {

    RUNNING,
    STOPPED;

    /**
     * Returns enumeration of given name.
     *
     * @param name name of enumeration, as passed in it constructor
     * @return enumeration itself
     * @throws DeploymentStateParserException if it is not possible to parse enumeration from given name
     */
    public static DeploymentState parse(String name) throws DeploymentStateParserException {
        try {
            return DeploymentState.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DeploymentStateParserException(String.format("Unable to parse '%s' to deployment state enumeration.", name));
        }
    }
}
