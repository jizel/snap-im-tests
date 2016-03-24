package travel.snapshot.qa.test.execution.load

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.maven.MavenExecutor
import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.Properties

class LoadTestsExecution {

    private LoadTestsConfiguration configuration

    private LoadTestsSimulation simulation

    LoadTestsExecution() {
    }

    LoadTestsExecution(LoadTestsSimulation simulation, LoadTestsConfiguration configuration) {
        this.simulation = simulation
        this.configuration = configuration
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

        MavenExecutor mavenExecutor = Spacelift.task(MavenExecutor)
                .workingDir(ProjectHelper.loadTestsProjectDir)
                .pom(projectPom)
                .goal("clean")
                .goal("test")
                .goal("gatling:execute")
                .property("gatling.failOnError=false")
                .property("gatling.resultsFolder=${ProjectHelper.getLoadTestsResultsDir(simulation)}")
                .property("gatling.debugPort=8001")
                .property("gatling.simulationClass=${simulation}")
                .property("environment=${configuration.environment}")
                .property("startUsers=${configuration.startUsers}")
                .property("endUsers=${configuration.endUsers}")
                .property("ramp=${configuration.ramp}")

        String host = Properties.LoadTest.host

        if (host) {
            mavenExecutor.property("host=${host}")
        }

        if (!host && Properties.LoadTest.environment == LoadTestEnvironment.DOCKER) {
            // this happens when we are running against Docker AND we have not set 'loadTestHost' property
            // in such case we will resolve Tomcat container IP, both for HOST and MACHINE cases
            mavenExecutor.property("host=${Properties.Docker.getIp(ServiceType.TOMCAT.name().toLowerCase())}")
        }

        mavenExecutor.execute().await()
    }

    private def getProjectPom() {
        new File(ProjectHelper.loadTestsProjectDir, "pom.xml").absolutePath
    }
}
