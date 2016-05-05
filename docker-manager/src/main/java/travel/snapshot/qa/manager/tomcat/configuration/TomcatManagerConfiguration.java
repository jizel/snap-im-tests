package travel.snapshot.qa.manager.tomcat.configuration;

import static travel.snapshot.qa.manager.tomcat.configuration.HTTPScheme.HTTP;
import static travel.snapshot.qa.manager.tomcat.configuration.HTTPScheme.HTTPS;

import travel.snapshot.qa.manager.api.configuration.AbstractConfigurationBuilder;
import travel.snapshot.qa.manager.api.configuration.Configuration;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.api.container.ContainerManagerConfigurationException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TomcatManagerConfiguration implements Configuration {

    private final File javaHome;
    private final String javaVmArguments;
    private final File catalinaHome;
    private final File catalinaBase;
    private final int startupTimeoutInSeconds;
    private final String serverConfig;
    private final String loggingProperties;
    private final boolean outputToConsole;
    private final String user;
    private final String password;
    private final Charset urlCharset;
    private final URL managerUrl;
    private final HTTPScheme managerScheme;
    private final String managerHost;
    private final int managerPort;
    private final boolean remote;

    private TomcatManagerConfiguration(Builder builder) {
        javaHome = builder.javaHome;
        javaVmArguments = builder.javaVmArguments;
        catalinaHome = builder.catalinaHome;
        catalinaBase = builder.catalinaBase;
        startupTimeoutInSeconds = builder.startupTimeoutInSeconds;
        serverConfig = builder.serverConfig;
        loggingProperties = builder.loggingProperties;
        outputToConsole = builder.outputToConsole;
        user = builder.user;
        password = builder.password;
        urlCharset = builder.urlCharset;
        managerUrl = builder.managerUrl;
        managerScheme = builder.managerScheme;
        managerHost = builder.managerHost;
        managerPort = builder.managerPort;
        remote = builder.remote;
    }

    public static final class Builder extends AbstractConfigurationBuilder {

        public static final int DEFAULT_HTTP_PORT = 8080;

        public static final int DEFAULT_HTTPS_PORT = 8443;

        private File javaHome = resolveProperty("java.home", "JAVA_HOME");

        private String javaVmArguments = "-Xmx512m -XX:MaxPermSize=128m";

        private File catalinaHome = resolveProperty("catalina.home", "CATALINA_HOME");

        private File catalinaBase = resolveProperty("catalina.base", "CATALINA_BASE");

        private String bindAddress = "127.0.0.1";

        private int bindHttpPort = 8080;

        private int startupTimeoutInSeconds = 120;

        private String serverConfig = "server.xml";

        private String loggingProperties = "logging.properties";

        private boolean outputToConsole = true;

        private String user = "admin";

        private String password = "admin";

        private Charset urlCharset = StandardCharsets.ISO_8859_1;

        private URL managerUrl;

        private HTTPScheme managerScheme;

        private String managerHost;

        private int managerPort;

        private HTTPScheme httpScheme = HTTP;

        private boolean remote = false;

        public Builder() {
            bindAddress = resolveHostIp();
        }

        public Builder setUser(final String user) {
            this.user = user;
            return this;
        }

        public Builder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder setUrlCharset(final Charset urlCharset) {
            this.urlCharset = urlCharset;
            return this;
        }

        public Builder setCatalinaHome(final String catalinaHome) {
            this.catalinaHome = new File(catalinaHome);
            return this;
        }

        public Builder setCatalinaBase(final String catalinaBase) {
            this.catalinaBase = new File(catalinaBase);
            return this;
        }

        public Builder setJavaHome(final String javaHome) {
            if (javaHome != null && new File(javaHome).isDirectory()) {
                this.javaHome = new File(javaHome);
            }
            return this;
        }

        /**
         * This will override the default ("-Xmx512m -XX:MaxPermSize=128m") startup JVM arguments.
         *
         * @param javaVmArguments use as start up arguments
         */
        public Builder setJavaVmArguments(final String javaVmArguments) {
            this.javaVmArguments = javaVmArguments;
            return this;
        }

        public Builder setStartupTimeoutInSeconds(final int startupTimeoutInSeconds) {
            this.startupTimeoutInSeconds = startupTimeoutInSeconds;
            return this;
        }

        public Builder setServerConfig(final String serverConfig) {
            this.serverConfig = serverConfig;
            return this;
        }

        public Builder setLoggingProperties(final String loggingProperties) {
            this.loggingProperties = loggingProperties;
            return this;
        }

        public Builder setOutputToConsole(final boolean outputToConsole) {
            this.outputToConsole = outputToConsole;
            return this;
        }

        public Builder setBindAddress(String bindAddress) {
            this.bindAddress = bindAddress;
            return this;
        }

        public Builder setBindPort(int bindPort) {
            validatePort(bindPort);
            this.bindHttpPort = bindPort;
            return this;
        }

        /**
         * Sets HTTP scheme to use. In case HTTPS scheme is set, bind port is automatically set to {@link
         * #DEFAULT_HTTPS_PORT} otherwise it is automatically set to {@link #DEFAULT_HTTP_PORT}.
         *
         * You have to call method {@link #setBindPort(int)} in case you want to override port set here after this is
         * set.
         *
         * @param httpScheme http scheme to use
         */
        public Builder setHttpScheme(HTTPScheme httpScheme) {
            this.httpScheme = httpScheme;

            if (httpScheme == HTTPS) {
                bindHttpPort = DEFAULT_HTTPS_PORT;
            } else {
                bindHttpPort = DEFAULT_HTTP_PORT;
            }

            return this;
        }

        /**
         * Sets remote flag to true, it means that Tomcat container is running on other host
         *
         * @return this builder
         */
        public Builder remote() {
            remote = true;
            return this;
        }

        public TomcatManagerConfiguration build() {
            validate();
            return new TomcatManagerConfiguration(this);
        }

        private void validate() throws ContainerManagerConfigurationException {

            if (!remote) {
                Validate.configurationDirectoryExists(catalinaHome,
                        "Either \"CATALINA_HOME\" environment variable or \"catalina.home\" system property in " +
                                "TomcatManagerConfiguration must be set and point to a valid directory! " +
                                catalinaHome + " is not valid directory!");

                Validate.configurationDirectoryExists(javaHome,
                        "Either \"JAVA_HOME\" environment variable or \"java.home\" system property in " +
                                "TomcatManagerConfiguration must be set and point to a valid directory! " +
                                javaHome + " is not valid directory!");

                if (this.catalinaBase == null) {
                    this.catalinaBase = this.catalinaHome.getAbsoluteFile();
                }

                Validate.isValidFile(catalinaBase.getAbsolutePath() + "/conf/" + serverConfig,
                        "The server configuration file denoted by serverConfig property has to exist! This file: "
                                + catalinaBase.getAbsolutePath() + "/conf/" + serverConfig + " does not!");
            }

            Validate.notNullOrEmpty(bindAddress, "Bind address must not be null or empty");

            this.managerUrl = createManagerUrl();
            this.managerScheme = HTTPScheme.valueOf(managerUrl.getProtocol().toUpperCase());
            this.managerHost = managerUrl.getHost();
            this.managerPort = resolvePort(managerUrl, managerScheme);
        }

        private static File resolveProperty(String propertyName, String envPropertyName) {
            String property = System.getProperty(propertyName);

            if (Validate.isNullOrEmpty(property)) {
                property = System.getenv(envPropertyName);
            }

            return property == null ? null : new File(property);
        }

        private URL createManagerUrl() throws ContainerManagerConfigurationException {
            final String urlString = String.format("%s://%s:%d/manager", httpScheme, bindAddress, bindHttpPort);

            try {
                return new URL(urlString);
            } catch (final MalformedURLException ex) {
                throw new ContainerManagerConfigurationException(String.format("Manager URL is not valid: %s", urlString),
                        ex);
            }
        }

        private int resolvePort(URL managerUrl, HTTPScheme scheme) {
            Validate.notNull(scheme, "HTTP scheme to resolve default port for must not be a null object.");

            int port = managerUrl.getPort();

            if (port == -1) {
                if (scheme == HTTP) {
                    return 8080;
                } else {
                    return 8443;
                }
            } else {
                return port;
            }
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Charset getUrlCharset() {
        return urlCharset;
    }

    public URL getManagerUrl() {
        return managerUrl;
    }

    public String getManagerHost() {
        return managerHost;
    }

    public HTTPScheme getManagerScheme() {
        return managerScheme;
    }

    public int getManagerPort() {
        return managerPort;
    }

    public File getCatalinaHome() {
        return catalinaHome;
    }

    public File getCatalinaBase() {
        return catalinaBase == null ? catalinaHome : catalinaBase;
    }

    public String getJavaHome() {
        try {
            return javaHome.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get canonical path of " + javaHome, e);
        }
    }

    public String getJavaBin() {
        if (getJavaHome() != null) {
            return getJavaHome() + File.separatorChar + "bin" + File.separatorChar + "java";
        } else {
            return "java";
        }
    }

    public String getJavaVmArguments() {
        return javaVmArguments;
    }

    public int getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    public String getServerConfig() {
        return serverConfig;
    }

    public String getLoggingProperties() {
        return loggingProperties;
    }

    public boolean isOutputToConsole() {
        return outputToConsole;
    }

    public boolean isRemote() {
        return this.remote;
    }
}
