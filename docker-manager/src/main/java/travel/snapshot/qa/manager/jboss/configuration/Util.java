package travel.snapshot.qa.manager.jboss.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    /**
     * Resolves a location of a JBoss installation either from a system property with name {@code jboss.home} or from an
     * environment variable of name {@code JBOSS_HOME}. System property overrides environment property.
     *
     * @return location of JBoss installation
     */
    public static String getJBossHome() {

        String home = System.getProperty("jboss.home");

        if (home == null) {
            home = System.getenv("JBOSS_HOME");
        }

        return home;
    }

    /**
     * Resolves a container type from a system property of name {@code jboss.container.type}. Default container type is
     * {@link ContainerType#WILDFLY}.
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
