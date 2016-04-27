package travel.snapshot.qa.test.execution.load

enum LoadTestEnvironment {

    LOCAL("local"),
    PRODUCTION("production"),
    DEVELOPMENT("development"),
    TESTING("testing"),
    DOCKER("docker"),
    NGINX("nginx")

    private final String name

    LoadTestEnvironment(String name) {
        this.name = name
    }

    @Override
    String toString() {
        name
    }
}
