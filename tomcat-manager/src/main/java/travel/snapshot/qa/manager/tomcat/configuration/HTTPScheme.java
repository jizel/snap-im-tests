package travel.snapshot.qa.manager.tomcat.configuration;

public enum HTTPScheme {
    HTTP("http"),
    HTTPS("https");

    private final String name;

    HTTPScheme(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
