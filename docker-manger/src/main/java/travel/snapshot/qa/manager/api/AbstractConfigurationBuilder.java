package travel.snapshot.qa.manager.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public abstract class AbstractConfigurationBuilder {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private static final String DEFAULT_RESOLVED_IP = "127.0.0.1";

    public static final String DOCKER_HOST_ENV_PROPERTY_NAME = "DOCKER_HOST";

    public static final String DOCKER_HOST_SYSTEM_PROPERTY_NAME = "docker.host";

    /**
     * Resolve Docker host. Address of the Docker host differs in case Docker machine is used. In that case, we need to
     * communicate from the host we are executing Docker managers to the Docker host machine instance which sits in
     * another network instead of the localhost one in docker-compose / docker scenario.
     *
     * This will be automatically propagated to the configuration classes of each respective service so default IP will
     * be either localhost one or the one resolved from here so no subsequent server address setting needs to be done.
     *
     * @return resolved Docker host
     */
    public String resolveHostIp() {

        String resolvedHostIp = DEFAULT_RESOLVED_IP;
        String hostIpEnvironmentProperty = getDockerHostEnvProperty();
        String hostIpProperty = getDockerHostSystemProperty();

        if (hostIpEnvironmentProperty != null && !hostIpEnvironmentProperty.isEmpty()) {
            try {
                resolvedHostIp = extractDockerHostIp(hostIpEnvironmentProperty);
            } catch (IllegalArgumentException ex) {
                logger.warn("Unable to extract IP address from DOCKER_HOST environment property.");
            }
        }

        if (hostIpProperty != null && !hostIpProperty.isEmpty()) {
            resolvedHostIp = hostIpProperty;
        }

        return resolvedHostIp;
    }

    /**
     * Extracts host from the specified {@code dockerHostIpEnvironmentProperty}. Usually, it is set as environment
     * property of form {@code tcp://192.168.99.100:2376} so this effectively returns {@code 192.168.99.100}.
     *
     * @param dockerHostIpEnvironmentProperty property to extract host from
     * @return extracted host from the {@code dockerHostIpEnvironmentProperty}
     */
    public String extractDockerHostIp(String dockerHostIpEnvironmentProperty) {
        URI uri;

        try {
            uri = new URI(dockerHostIpEnvironmentProperty);
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("Unable to extract IP address or host name from %s",
                    dockerHostIpEnvironmentProperty),
                    ex);
        }

        return uri.getHost();
    }

    /**
     * @return value of {@code DOCKER_HOST} or null if not set
     */
    public String getDockerHostEnvProperty() {
        return System.getenv(DOCKER_HOST_ENV_PROPERTY_NAME);
    }

    /**
     * @return value of {@code docker.host} or null if not set
     */
    public String getDockerHostSystemProperty() {
        return System.getProperty(DOCKER_HOST_SYSTEM_PROPERTY_NAME);
    }

    /**
     * Validates if port is in range 0 - 65535 inclusive.
     *
     * @param port port to validate
     * @throws ConfigurationException in case port is not in that range
     */
    public void validatePort(int port) {
        if (port < 0 || port > 65535) {
            throw new ConfigurationException(String.format("Specified port is invalid: %s", port));
        }
    }
}
