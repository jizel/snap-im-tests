package travel.snapshot.qa.manager.jboss.configuration;

import static travel.snapshot.qa.manager.Util.isNotNullAndEmpty;

import java.util.Arrays;
import java.util.List;

public final class Configuration {

    private final String serverGroup;

    private final long domainStartTimeout;

    private final List<String> domainServers;

    private final String domainMasterHostName;

    private final String domainConfig;

    private final String hostConfig;

    private final String standaloneConfig;

    private Configuration(Builder builder) {
        this.serverGroup = builder.serverGroup;
        this.domainStartTimeout = builder.domainStartTimeout;
        this.domainServers = builder.domainServers;
        this.domainMasterHostName = builder.domainMasterHostName;
        this.domainConfig = builder.domainConfig;
        this.hostConfig = builder.hostConfig;
        this.standaloneConfig = builder.standaloneConfig;
    }

    public String getServerGroup() {
        return serverGroup;
    }

    public long getDomainStartTimeout() {
        return domainStartTimeout;
    }

    public List<String> getDomainServers() {
        return domainServers;
    }

    public String getDomainMasterHostName() {
        return domainMasterHostName;
    }

    public String getDomainConfig() {
        return domainConfig;
    }

    public String getHostConfig() {
        return hostConfig;
    }

    public String getStandaloneConfig() {
        return standaloneConfig;
    }

    public static final class Builder {

        private static final String[] DEFAULT_DOMAIN_SERVERS = new String[]{
                "server-one",
                "server-two",
                "server-three"
        };

        private String serverGroup = System.getProperty("server.group", "main-server-group");

        private long domainStartTimeout = 120L;

        private List<String> domainServers = Arrays.asList(DEFAULT_DOMAIN_SERVERS);

        private String domainMasterHostName = "master";

        private String domainConfig = "domain.xml";

        private String hostConfig = "host.xml";

        private String standaloneConfig = "standalone.xml";

        public Builder setServerGroup(String serverGroup) {
            if (isNotNullAndEmpty(serverGroup)) {
                this.serverGroup = serverGroup;
            }

            return this;
        }

        public Builder setDomainStartTimeout(long domainStartTimeout) {
            if (domainStartTimeout > 0) {
                this.domainStartTimeout = domainStartTimeout;
            }
            return this;
        }

        public Builder setDomainServers(String domainServer, String... domainServers) {
            if (isNotNullAndEmpty(domainServer)) {
                this.domainServers.clear();
                this.domainServers.add(domainServer);
            }

            for (String domainServerArg : domainServers) {
                if (isNotNullAndEmpty(domainServer)) {
                    this.domainServers.add(domainServerArg);
                }
            }

            return this;
        }

        public Builder setDomainMasterHostName(String domainMasterHostName) {
            if (isNotNullAndEmpty(domainMasterHostName)) {
                this.domainMasterHostName = domainMasterHostName;
            }
            return this;
        }

        public Builder setDomainConfig(String domainConfig) {
            if (isNotNullAndEmpty(domainConfig)) {
                this.domainConfig = domainConfig;
            }
            return this;
        }

        public Builder setHostConfig(String hostConfig) {
            if (isNotNullAndEmpty(hostConfig)) {
                this.hostConfig = hostConfig;
            }
            return this;
        }

        public Builder setStandaloneConfig(String standaloneConfig) {
            if (isNotNullAndEmpty(standaloneConfig)) {
                this.standaloneConfig = standaloneConfig;
            }
            return this;
        }


        public Configuration build() {
            return new Configuration(this);
        }
    }
}
