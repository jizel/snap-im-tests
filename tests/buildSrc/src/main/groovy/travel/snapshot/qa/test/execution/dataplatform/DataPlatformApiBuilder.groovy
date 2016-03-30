package travel.snapshot.qa.test.execution.dataplatform

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.maven.MavenExecutor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DataPlatformApiBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DataPlatformApiBuilder)

    final String dataPlatformApiPath

    final boolean withoutTests

    DataPlatformApiBuilder(String dataPlatformApiPath, boolean withoutTests) {
        this.dataPlatformApiPath = dataPlatformApiPath
        this.withoutTests = withoutTests
    }

    def build() {
        logger.info("Building Data Platform API " + (withoutTests ? "without tests" : ""))

        MavenExecutor mavenExecutor = Spacelift.task(MavenExecutor)
                .workingDir(dataPlatformApiPath)
                .pom(projectPom)
                .goal("clean")
                .goal("install")

        if (withoutTests) {
            mavenExecutor.parameter("skipTests=true")
        }

        mavenExecutor.execute().await()

        logger.info("Building of whole Data Platform API has finished")
    }

    private String getProjectPom() {
        new File(dataPlatformApiPath, "pom.xml").absolutePath
    }
}
