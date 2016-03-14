package travel.snapshot.qa.test.execution.load

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.maven.MavenExecutor
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.PropertyResolver

class LoadTestsExecution {

    private final DataPlatformTestOrchestration orchestration

    private LoadTestsConfiguration configuration

    private LoadTestsSimulation simulation

    LoadTestsExecution(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration
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

        MavenExecutor mavenExecutor = Spacelift.task(MavenExecutor).pom(projectPom)
                .goal("clean")
                .goal("test")
                .goal("gatling:execute")
                .property("gatling.failOnError=false")
                .property("gatling.resultsFolder=${getResultsDir(simulation)}")
                .property("gatling.debugPort=8001")
                .property("gatling.simulationClass=${simulation}")
                .property("environment=${configuration.environment}")
                .property("startUsers=${configuration.startUsers}")
                .property("endUsers=${configuration.endUsers}")
                .property("ramp=${configuration.ramp}")

        String host = PropertyResolver.resolveLoadTestHost()

        if (host) {
            mavenExecutor.property("host=${host}")
        }

        if (!host && PropertyResolver.resolveLoadTestEnvironment() == LoadTestEnvironment.DOCKER) {
            // this happens when we are running against Docker AND we have not set 'loadTestHost' property
            // in such case we will resolve Tomcat container IP, both for HOST and MACHINE cases
            mavenExecutor.property("host=${PropertyResolver.resolveContainerIP(ServiceType.TOMCAT.name().toLowerCase(), orchestration.get())}")
        }

        mavenExecutor.execute().await()
    }

    private def getProjectDir() {
        new File(ProjectHelper.workspace, "dataplatformqa/load_tests").absolutePath
    }

    private def getResultsDir(LoadTestsSimulation simulation) {

        def reportDirName = simulation.toString().split("\\.").last()

        new File(ProjectHelper.workspace, "reports/load_tests/${reportDirName}").absolutePath
    }

    private def getProjectPom() {
        new File(getProjectDir(), "pom.xml").absolutePath
    }
}
