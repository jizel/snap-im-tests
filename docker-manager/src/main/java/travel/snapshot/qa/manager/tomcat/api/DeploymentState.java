package travel.snapshot.qa.manager.tomcat.api;

/**
 * Represents enumeration of states in which some deployment in a Tomcat container can be.
 */
public enum DeploymentState {

    RUNNING("running"),
    STOPPED("stopped");

    private String name;

    DeploymentState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns enumeration of given name.
     *
     * @param name name of enumeration, as passed in it constructor
     * @return enumeration itself
     * @throws DeploymentStateParserException if it is not possible to parse enumeration from given name
     */
    public static DeploymentState parse(String name) throws DeploymentStateParserException {
        for (DeploymentState state : values()) {
            if (state.toString().equals(name)) {
                return state;
            }
        }

        throw new DeploymentStateParserException(String.format("Unable to parse '%s' to deployment state enumeration.", name));
    }
}
