package travel.snapshot.qa.connection;

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
