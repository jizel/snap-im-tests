package travel.snapshot.qa.test.execution.dataplatform

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Builds modules in Data platform, optionally with or without tests and only these which are not already built.
 */
class DataPlatformBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformBuilder)

    final String dataPlatformPath

    final boolean withoutTests

    /**
     *
     * @param dataPlatformPath path to Data Platform repository
     * @param withoutTests true if tests should be skipped during build process, false otherwise
     */
    DataPlatformBuilder(String dataPlatformPath, boolean withoutTests) {
        this.dataPlatformPath = dataPlatformPath
        this.withoutTests = withoutTests
    }

    /**
     * Builds platform without tests.
     *
     * @param dataPlatfromPath path to Data platform repository
     */
    DataPlatformBuilder(String dataPlatfromPath) {
        this(dataPlatfromPath, true)
    }

    /**
     * Builds all modules, even there is their wars built
     *
     * @param dataPlatformModules modules to build
     */
    def forceBuild(DataPlatformModules dataPlatformModules) {
        build(dataPlatformModules.modules())
    }

    /**
     * Builds all modules, even there is their wars built
     *
     * @param dataPlatformModules modules to build
     */
    def forceBuild(List<DataPlatformModule> dataPlatformModules) {
        for (DataPlatformModule module : dataPlatformModules) {
            build(module, withoutTests)
        }
    }

    /**
     * Builds specified modules when not already built.
     *
     * @param dataPlatformModules modules to build
     */
    def build(DataPlatformModules dataPlatformModules) {
        build(dataPlatformModules.modules())
    }

    /**
     * Builds specified modules when not already built
     *
     * @param dataPlatformModules modules to build
     */
    def build(List<DataPlatformModule> dataPlatformModules) {
        def unbuiltModules = getUnbuilt(dataPlatformModules)

        for (DataPlatformModule unbuiltModule : unbuiltModules) {
            build(unbuiltModule, withoutTests)
        }
    }

    /**
     * Builds a module.
     *
     * @param module module to build
     * @param withoutTests set to true when test task during build should be skipped
     */
    def build(DataPlatformModule module, boolean withoutTests) {

        logger.info("Building module ${module.path}")

        CommandTool gradleBuild = Spacelift.task("gradle")
                .parameters("--project-dir", dataPlatformPath)
                .parameter("${module.path}:build")

        if (withoutTests) {
            gradleBuild.parameters("-x", "test")
        }

        gradleBuild.execute().await()

        logger.info("Building of module ${module.path} has finished")
    }

    /**
     * Builds whole data-platform project, regardless of previously added modules
     *
     * @param withoutTests
     */
    def buildProject(boolean withoutTests) {
        logger.info("Building whole Data Platform " + (withoutTests ? "without tests" : ""))

        CommandTool gradleBuild = Spacelift.task("gradle")
                .parameters("--project-dir", dataPlatformPath)
                .parameter("build")

        if (withoutTests) {
            gradleBuild.parameters("-x", "test")
        }

        gradleBuild.execute().await()

        logger.info("Building of whole Data Platform has finished")
    }

    /**
     * Builds a module without tests.
     *
     * @param module module to build
     */
    def build(DataPlatformModule module) {
        build(module, true)
    }

    /**
     * Checks if all modules are built or not
     *
     * @param modules modules to check
     * @return true if all modules are built, false otherwise
     */
    boolean areBuilt(DataPlatformModules modules) {
        getUnbuilt(modules.modules())
    }

    /**
     * Checks if all modules are built or not
     *
     * @param modules list of modules to check
     * @return true if all modules are built, false otherwise
     */
    boolean areBuilt(List<DataPlatformModule> modules) {
        getUnbuilt(modules).isEmpty()
    }

    boolean isBuilt(DataPlatformModule module) {
        isWarPresent(dataPlatformPath, module.war)
    }

    /**
     * Gets modules which are not already built with their (possibly also unbuilt) dependencies.
     *
     * @param modules modules to filter
     * @return unbuilt modules
     */
    List<DataPlatformModule> getUnbuilt(List<DataPlatformModule> modules) {

        def unbuilt = []

        for (DataPlatformModule module : modules) {

            for (DataPlatformModule dependency : module.dependencies) {
                if (!isWarPresent(dataPlatformPath, dependency)) {
                    unbuilt << dependency
                }
            }

            if (!isWarPresent(dataPlatformPath, module)) {
                unbuilt << module
            }
        }

        unbuilt.unique()
    }

    /**
     * Gets modules which are not already built.
     *
     * @param modules modules to filter
     * @return unbuilt modules
     */
    List<DataPlatformModule> getUnbuilt(DataPlatformModules modules) {
        getUnbuilt(modules.modules())
    }

    /**
     * Gets modules which are already built.
     *
     * @param modules modules to filter
     * @return built modules
     */
    List<DataPlatformModule> getBuilt(List<DataPlatformModule> modules) {

        def built = []

        for (DataPlatformModule module : modules) {
            if (isWarPresent(dataPlatformPath, module)) {
                built << module
            }
        }

        built
    }

    /**
     * Gets modules which are not already built.
     *
     * @param modules modules to filter
     * @return unbuilt modules
     */
    List<DataPlatformModule> getBuilt(DataPlatformModules modules) {
        getBuilt(modules.modules())
    }

    /**
     * Checks if some module is already built or not
     *
     * @param dataPlatformPath path to data platform repository
     * @param module module to check the existency of its war file
     * @return true if war file exists, false otherwise
     */
    private boolean isWarPresent(String dataPlatformPath, DataPlatformModule module) {
        new File(dataPlatformPath, module.war).exists()
    }
}
