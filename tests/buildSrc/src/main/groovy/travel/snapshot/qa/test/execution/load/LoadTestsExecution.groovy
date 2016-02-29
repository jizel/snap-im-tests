package travel.snapshot.qa.test.execution.load

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.maven.MavenExecutor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.DataPlatformTestOrchestration

class LoadTestsExecution {

    private static final Logger logger = LoggerFactory.getLogger(LoadTestsExecution)

    private final DataPlatformTestOrchestration orchestration

    private final File workspace

    private LoadTestsConfiguration configuration

    private LoadTestsSimulation simulation

    LoadTestsExecution(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration
        this.workspace = new GradleSpaceliftDelegate().project().spacelift.workspace
    }

    LoadTestsExecution configuration(LoadTestsConfiguration configuration) {
        this.configuration = configuration
        this
    }

    LoadTestsExecution simulation(LoadTestsSimulation simulation) {
        this.simulation = simulation
        this
    }

    def execute() {

        if (!configuration) {
            throw new IllegalStateException("Load test configuration was not set.")
        }

        if (!simulation) {
            throw new IllegalStateException("Load test simulation was not set.")
        }

        logger.info("Load test configuration to be used: {}, Load test simulation to be used: {}", configuration, simulation)

        Spacelift.task(MavenExecutor).pom(projectPom)
                .goal("clean")
                .goal("test")
                .goal("gatling:execute")
                .property("environment=${configuration.environment}")
                .property("gatling.simulation=${simulation}")
                .property("startUsers=${configuration.startUsers}")
                .property("endUsers=${configuration.endUsers}")
                .property("ramp=${configuration.ramp}")
                .execute()
                .await()
    }

    private def getProjectDir() {
        new File(workspace, "dataplatformqa/load_tests").absolutePath
    }

    private def getProjectPom() {
        new File(getProjectDir(), "pom.xml").absolutePath
    }
}
