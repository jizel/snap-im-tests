package travel.snapshot.qa.test.execution

/**
 * Abstraction of module in Data platform repository
 */
class DataPlatformModule {

    String name
    String path
    String war

    /**
     * Path to that module from the root of the repository is equal to its name.
     *
     * @param name name of module, e.g. ConfigurationModule
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     */
    DataPlatformModule(String name, String war) {
        this(name, name, war)
    }

    /**
     *
     * @param name name of module, e.g. ConfigurationModule
     * @param path path to that module from the root of the repository
     * @param war path of war of that module, e.g. ConfigurationModule/build/libs/ConfigurationModule-1.0.war
     */
    DataPlatformModule(String name, String path, String war) {
        this.name = name
        this.path = path
        this.war = war
    }

    @Override
    String toString() {
        String.format("Name: %s, Path: %s, War: %", name, path, war)
    }
}
