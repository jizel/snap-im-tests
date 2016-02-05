package travel.snapshot.qa.test.execution

/**
 * Encapsulates module builing
 */
class DataPlatformBuildExecutor {

    String dataPlatformPath = "snapshot/workspace/data-platform"

    List<DataPlatformModule> modules = []

    boolean withoutTests = true

    boolean force = false

    /**
     *
     * @param dataPlatformPath path to data-platform repository
     * @return this
     */
    DataPlatformBuildExecutor dataPlatform(String dataPlatformPath) {
        this.dataPlatformPath = dataPlatformPath
        this
    }

    /**
     * when called, all modules will be tested during build, this is skipped by default
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
    DataPlatformBuildExecutor with(DataPlatformModules modules) {
        this.modules.addAll(modules.modules())
        this
    }

    /**
     *
     * @param modules modules to build
     * @return this
     */
    DataPlatformBuildExecutor with(List<DataPlatformModule> modules) {
        this.modules.addAll(modules)
        this
    }

    DataPlatformBuildExecutor with(DataPlatformModule module) {
        modules << module
        this
    }

    /**
     * Builds all added modules
     *
     * @return this
     */
    DataPlatformBuildExecutor execute() {
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
        def dataPlatformBuilder = new DataPlatformBuilder(dataPlatformPath, withoutTests)

        if (force) {
            dataPlatformBuilder.forceBuild(modules)
        } else {
            dataPlatformBuilder.build(modules)
        }

        this
    }
}
