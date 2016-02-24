package travel.snapshot.qa.test.execution.dataplatform

import travel.snapshot.qa.util.PropertyResolver

/**
 * Encapsulates module builing
 */
class DataPlatformBuildExecutor {

    File dataPlatformDir

    List<DataPlatformModule> modules = []

    boolean withoutTests = true

    boolean force = false

    DataPlatformBuildExecutor() {
        this.dataPlatformDir = PropertyResolver.resolveDataPlatformRepositoryLocation()
    }

    /**
     *
     * @param dataPlatformPath path to data-platform repository
     * @return this
     */
    DataPlatformBuildExecutor dataPlatform(String dataPlatformPath) {
        this.dataPlatformDir = PropertyResolver.resolveDataPlatformRepositoryLocation(dataPlatformPath)
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
                this.modules << module
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
        execute(force)
        this
    }

    /**
     * Builds all added modules.
     *
     * @param force if set to true, even already built modules will be build again
     * @return this
     */
    DataPlatformBuildExecutor execute(boolean force) {
        execute(withoutTests, force)
        this
    }

    /**
     * Builds all added modules
     *
     * @param withoutTests true if tests should be skipped, false otherwise
     * @param force true if even built modules should be built again
     * @return this
     */
    DataPlatformBuildExecutor execute(boolean withoutTests, boolean force) {

        def dataPlatformBuilder = new DataPlatformBuilder(dataPlatformDir.absolutePath, withoutTests)

        if (modules.size() == 1 && modules.get(0) == DataPlatformModule.PROJECT) {
            dataPlatformBuilder.buildProject(withoutTests)
            return this
        }

        if (force) {
            dataPlatformBuilder.forceBuild(modules)
        } else {
            dataPlatformBuilder.build(modules)
        }

        this
    }
}
