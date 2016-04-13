package travel.snapshot.qa.manager.jboss.configuration;

public final class Management {

    private final String managementProtocol;

    private final String managementAddress;

    private final int managementPort;

    private Management(Builder builder) {
        this.managementProtocol = builder.managementProtocol;
        this.managementAddress = builder.managementAddress;
        this.managementPort = builder.managementPort;
    }

    public String getManagementProtocol() {
        return managementProtocol;
    }

    public String getManagementAddress() {
        return managementAddress;
    }

    public int getManagementPort() {
        return managementPort;
    }

    public static final class Builder {

        private String managementProtocol = "http-remoting";

        private String managementAddress = "127.0.0.1";

        private int managementPort = 9990;

        public Builder setManagementProtocol(String managementProtocol) {
            this.managementProtocol = managementProtocol;
            return this;
        }

        public Builder setManagementAddress(String managementAddress) {
            this.managementAddress = managementAddress;
            return this;
        }

        public Builder setManagementPort(int managementPort) {
            this.managementPort = managementPort;
            return this;
        }

        public Management build() {
            return new Management(this);
        }
    }
}
