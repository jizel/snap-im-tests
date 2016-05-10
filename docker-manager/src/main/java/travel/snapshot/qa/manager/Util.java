package travel.snapshot.qa.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.jboss.configuration.ContainerType;

public final class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Resolves a location of a JBoss installation either from a system property with name jboss.containerType.home or
     * from an environment variable of name {@code JBOSS_HOME} when such property is not set. System property overrides
     * environment property.
     *
     * @param containerType type of container to get home of
     * @return location of JBoss installation
     */
    public static String getJBossHome(ContainerType containerType) {

        String home = System.getProperty("jboss." + containerType.name().toLowerCase() + ".home");

        if (home == null) {
            home = System.getenv("JBOSS_HOME");
        }

        return home;
    }

    /**
     * Gets home of JBoss container. Container type to get is resolved from {@link #getContainerType()}
     *
     * @return home of JBoss container according to its type
     * @see #getContainerType()
     */
    public static String getJBossHome() {
        return getJBossHome(getContainerType());
    }

    /**
     * Get home of Tomcat container either from system property "catalina.home" or environment variable "CATALINA_HOME"
     * when not set. System property overrides environment property.
     */
    public static String getCatalinaHome() {
        String home = System.getProperty("catalina.home");

        if (home == null) {
            home = System.getenv("CATALINA_HOME");
        }

        return home;
    }

    /**
     * Resolves a container type from a system property of name {@code jboss.container.type} which value has to be the
     * same as the name of the container type enumeration. Default container type is {@link ContainerType#WILDFLY}.
     *
     * @return resolved type of a JBoss container
     */
    public static ContainerType getContainerType() {

        final ContainerType defaultContainerType = ContainerType.WILDFLY;

        String containerTypeName = System.getProperty("jboss.container.type");

        if (containerTypeName == null) {
            return defaultContainerType;
        }

        ContainerType containerType;

        try {
            containerType = Enum.valueOf(ContainerType.class, containerTypeName);
        } catch (IllegalArgumentException ex) {
            logger.info("Unable to resolve container type from value '%s'. Default container type of '%s' is returned.",
                    containerTypeName, defaultContainerType.toString());

            containerType = defaultContainerType;
        }

        return containerType;
    }

    public static boolean isNotNullAndEmpty(String testString) {
        return testString != null && testString.isEmpty();
    }
}
