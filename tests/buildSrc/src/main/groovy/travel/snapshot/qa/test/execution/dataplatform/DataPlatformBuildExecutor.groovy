package travel.snapshot.qa.test.execution.dataplatform

import travel.snapshot.qa.util.PropertyResolver

/**
 * Encapsulates module builing
 */
class DataPlatformBuildExecutor {

    File dataPlatformDir

    List<DataPlatformModule> modules = []

    boolean withoutTests = true

    DataPlatformBuildExecutor() {
        this.dataPlatformDir = PropertyResolver.resolveDataPlatformRepositoryLocation()
    }

    /**
     *
     * @param dataPlatformPath path to data-platform repository
     * @return this
     */
    DataPlatformBuildExecutor dataPlatform(String dataPlatformPath) {
        this.dataPlatformDir = PropertyResolver.resolveLocation(dataPlatformPath)
        this
    }

    /**
     * When called, all modules will be tested during build, this is skipped by default.
     *
     * @return this
     */
    DataPlatformBuildExecutor withTests() {
        withoutTests = false
        this
    }

    /**
     *
     * @param modules modules to build
     * @return this
     */
    DataPlatformBuildExecutor build(DataPlatformModules modules) {
        build(modules.modules())
        this
    }

    /**
     *
     * @param modules modules to build
     * @return this
     */
    DataPlatformBuildExecutor build(List<DataPlatformModule> modules) {
        for (DataPlatformModule module : modules) {
            build(module)
        }
        this
    }

    /**
     *
     * @param module module to build
     * @return this
     */
    DataPlatformBuildExecutor build(DataPlatformModule... modules) {
        if (modules) {
            for (DataPlatformModule module : modules) {
                if (module) {
                    if (!this.modules.contains(module)) {
                        this.modules << module
                    }
                }
            }
        }
        this
    }

    /**
     * Builds all added modules.
     *
     * @return this
     */
    DataPlatformBuildExecutor execute() {
        execute(withoutTests)
        this
    }

    /**
     * Builds all added modules
     *
     * @param withoutTests true if tests should be skipped, false otherwise
     * @param force true if even built modules should be built again
     * @return this
     */
    DataPlatformBuildExecutor execute(boolean withoutTests) {

        def dataPlatformBuilder = new DataPlatformBuilder(dataPlatformDir.absolutePath, withoutTests)

        if (modules.size() == 1 && modules.get(0) == DataPlatformModule.PROJECT) {
            dataPlatformBuilder.buildProject(withoutTests)
            return this
        }

        // from the test point of view, we have to build modules which are runtime prerequisities in Tomcat context
        // for example, Review, in runtime, depends on Identity module. We have to be sure that this module is
        // built and deployed along with Review module itself. If Review module was built and depoyed alone,
        // tests would fail because Review module depends internally on it.

        List<DataPlatformModule> dependencyModules = modules.collect { module -> module.dependencies }.flatten().unique()

        List<DataPlatformModule> modulesToBuild = []

        modulesToBuild.addAll(modules)
        modulesToBuild.addAll(dependencyModules)

        dataPlatformBuilder.build(modulesToBuild.unique())

        this
    }
}
