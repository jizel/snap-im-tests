package travel.snapshot.qa.manager.jboss.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JBossManagerConfiguration implements travel.snapshot.qa.manager.api.configuration.Configuration {

    private final JVM jvm;

    private final Configuration configuration;

    private final Authentication authentication;

    private final Management management;

    private final boolean isDomain;

    private final boolean remote;

    private final ContainerType containerType;

    private final int startupTimeoutInSeconds;

    private final boolean outputToConsole;

    private final List<String> processControllerJavaOpts;

    private final List<String> hostControllerJavaOpts;

    private final List<String> serverJavaOpts;

    private JBossManagerConfiguration(Builder builder) {
        this.jvm = builder.jvm;
        this.configuration = builder.configuration;
        this.authentication = builder.authentication;
        this.management = builder.management;
        this.isDomain = builder.isDomain;
        this.remote = builder.remote;
        this.containerType = builder.containerType;
        this.startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
        this.outputToConsole = builder.outputToConsole;
        this.processControllerJavaOpts = builder.processControllerJavaOpts;
        this.hostControllerJavaOpts = builder.hostControllerJavaOpts;
        this.serverJavaOpts = builder.serverJavaOpts;

        if (!new File(getJBossBaseDir()).exists()) {
            throw new IllegalStateException("Could not determine the value of JBoss base directory.");
        }

        if (!new File(getJBossConfigDir()).exists()) {
            throw new IllegalStateException("Could not determine the value of JBoss configuration directory.");
        }
    }

    public JVM getJVM() {
        return jvm;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Management getManagement() {
        return management;
    }

    public boolean isDomain() {
        return isDomain;
    }

    public boolean isRemote() {
        return remote;
    }

    public String getJBossBaseDir() {
        if (isDomain()) {
            return jvm.getJBossHome() + "/domain";
        } else {
            return jvm.getJBossHome() + "/standalone";
        }
    }

    public String getJBossLogDir() {
        return getJBossBaseDir() + "/log";
    }

    public String getJBossConfigDir() {
        return getJBossBaseDir() + "/configuration";
    }

    public String getJBossModuleDir() {
        return jvm.getJBossHome() + "/modules";
    }

    public List<String> getJavaOpts() {
        if (jvm.getJavaOpts().size() == 0 && containerType != null) {
            return containerType.javaOptions(this);
        }

        return jvm.getJavaOpts();
    }

    public List<String> getProcessControllerJavaOpts() {
        if (processControllerJavaOpts.size() == 0 && containerType != null) {
            return containerType.javaOptions(this);
        }
        return processControllerJavaOpts;
    }

    public List<String> getHostControllerJavaOpts() {
        if (hostControllerJavaOpts.size() == 0 && containerType != null) {
            return containerType.javaOptions(this);
        }

        return hostControllerJavaOpts;
    }

    public List<String> getServerJavaOpts() {
        return serverJavaOpts;
    }

    public int getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    public boolean isOutputToConsole() {
        return outputToConsole;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    public static final class Builder {

        private JVM jvm = new JVM.Builder().build();

        private Configuration configuration = new Configuration.Builder().build();

        public Authentication authentication = new Authentication.Builder().build();

        public Management management = new Management.Builder().build();

        private boolean isDomain = false;

        private boolean remote = false;

        private boolean outputToConsole = true;

        private ContainerType containerType = ContainerType.WILDFLY;

        private int startupTimeoutInSeconds = 120;

        private List<String> processControllerJavaOpts = new ArrayList<>();

        private List<String> hostControllerJavaOpts = new ArrayList<>();

        private List<String> serverJavaOpts = new ArrayList<>();

        public Builder domain() {
            isDomain = true;
            return this;
        }

        public Builder remote() {
            remote = true;
            return this;
        }

        public Builder setJVM(JVM jvm) {
            this.jvm = jvm;
            return this;
        }

        public Builder setConfiguration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder setAuthentication(Authentication authentication) {
            this.authentication = authentication;
            return this;
        }

        public Builder setManagement(Management management) {
            this.management = management;
            return this;
        }

        public Builder setContainerType(ContainerType containerType) {
            this.containerType = containerType;
            return this;
        }

        public Builder setContainerType(String containerType) throws IllegalArgumentException {
            this.containerType = ContainerType.valueOf(containerType);
            return this;
        }

        /**
         * @param startupTimeoutInSeconds timeout in seconds until start is considered unsuccessful, this value is
         *                                ignored when lower or equal to 0.
         * @return this
         */
        public Builder setStartupTimeoutInSeconds(int startupTimeoutInSeconds) {
            if (startupTimeoutInSeconds > 0) {
                this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            }
            return this;
        }

        public Builder setOutputToConsole(boolean outputToConsole) {
            this.outputToConsole = outputToConsole;
            return this;
        }

        public Builder setProcessControllerJavaOpts(String... processControllerJavaOpts) {
            this.processControllerJavaOpts.addAll(Arrays.asList(processControllerJavaOpts));
            return this;
        }

        public Builder setHostControllerJavaOpts(String... hostControllerJavaOpts) {
            this.hostControllerJavaOpts.addAll(Arrays.asList(hostControllerJavaOpts));
            return this;
        }

        public Builder setServerJavaOpts(String... serverJavaOpts) {
            this.serverJavaOpts.addAll(Arrays.asList(serverJavaOpts));
            return this;
        }

        public JBossManagerConfiguration build() {
            return new JBossManagerConfiguration(this);
        }
    }
}
