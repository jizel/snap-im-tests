package travel.snapshot.qa.connection;

/**
 * Enumeration of possible transport protocols for connection checking purposes.
 */
public enum Protocol {
    TCP("tcp"),
    UDP("udp");

    private final String name;

    Protocol(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
