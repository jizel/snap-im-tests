package travel.snapshot.qa.inspection;

/**
 * Enumeration of 'default' Docker network names.
 */
public enum DockerNetwork {

    HOST,
    BRIDGE,
    NULL;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
