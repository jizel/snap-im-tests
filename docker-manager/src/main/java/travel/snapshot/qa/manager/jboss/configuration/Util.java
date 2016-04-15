package travel.snapshot.qa.manager.jboss.configuration;

public final class Util {

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

    public static boolean isNotNullAndEmpty(String testString) {
        return testString != null && testString.isEmpty();
    }
}
