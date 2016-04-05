package travel.snapshot.qa.manager.jboss.util;

import travel.snapshot.qa.manager.jboss.configuration.ContainerType;

public class TestUtils {

    // default extracted container to target is JBoss AS 7
    private static final ContainerType defaultContainerType = ContainerType.WILDFLY;

    public static String getJBossHome() {

        String home = System.getProperty("jboss.home");

        if (home == null) {
            home = System.getenv("JBOSS_HOME");
        }

        return home;
    }

    public static ContainerType getContainerType() {

        String containerTypeName = System.getProperty("jboss.container.type");

        if (containerTypeName == null) {
            return defaultContainerType;
        }

        ContainerType containerType;

        try {
            containerType = Enum.valueOf(ContainerType.class, containerTypeName);
        } catch (IllegalArgumentException ex) {
            containerType = defaultContainerType;
        }

        return containerType;
    }
}

