package travel.snapshot.qa.test.execution.dataplatform

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Builds modules in Data platform.
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
     * Builds specified modules.
     *
     * @param dataPlatformModules modules to build
     */
    def build(DataPlatformModules dataPlatformModules) {
        build(dataPlatformModules.modules())
    }

    /**
     * Builds specified modules.
     *
     * @param dataPlatformModules modules to build
     */
    def build(List<DataPlatformModule> dataPlatformModules) {
        for (DataPlatformModule dataPlatformModule : dataPlatformModules) {
            build(dataPlatformModule, withoutTests)
        }
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
}
