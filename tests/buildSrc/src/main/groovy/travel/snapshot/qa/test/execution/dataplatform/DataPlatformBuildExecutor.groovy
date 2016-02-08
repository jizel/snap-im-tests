package travel.snapshot.qa.test.execution.dataplatform

/**
 * Encapsulates module builing
 */
class DataPlatformBuildExecutor {

    static String DEFAULT_DATA_PLATFORM_DIR = "data-platform"

    File workspace

    String dataPlatformDir = DEFAULT_DATA_PLATFORM_DIR

    List<DataPlatformModule> modules = []

    boolean withoutTests = true

    boolean force = false

    DataPlatformBuildExecutor(File workspace) {
        this(workspace, DEFAULT_DATA_PLATFORM_DIR)
    }

    DataPlatformBuildExecutor(File workspace, String dataPlatformDir) {
        this.workspace = workspace
        this.dataPlatformDir = dataPlatformDir
    }

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
        this.modules.addAll(modules.modules())
        this
    }

    /**
     *
     * @param modules modules to build
     * @return this
     */
    DataPlatformBuildExecutor build(List<DataPlatformModule> modules) {
        this.modules.addAll(modules)
        this
    }

    DataPlatformBuildExecutor build(DataPlatformModule module) {
        modules << module
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

        def dataPlatform = new File(workspace, dataPlatformDir).absolutePath

        def dataPlatformBuilder = new DataPlatformBuilder(dataPlatform, withoutTests)

        if (force) {
            dataPlatformBuilder.forceBuild(modules)
        } else {
            dataPlatformBuilder.build(modules)
        }

        this
    }
}
