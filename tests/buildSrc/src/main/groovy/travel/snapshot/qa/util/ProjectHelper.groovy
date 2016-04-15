package travel.snapshot.qa.util

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.SpaceliftExtension
import org.gradle.api.Project
import travel.snapshot.qa.test.execution.load.LoadTestsSimulation

import static travel.snapshot.qa.util.Properties.Docker.Installation.DOCKER_INSTALLATION

final class ProjectHelper {

    static boolean isLoadTestRunning() {
        !project.selectedTests.findAll { test -> test['name'] == "loadTests" }.isEmpty() && !Properties.Execution.skipTestExecution
    }

    static boolean isApiTestRunning() {
        !project.selectedTests.findAll { test ->
            test['name'].startsWith("apiTests") && test['name'] != "apiTestsBase"
        }.isEmpty() && !Properties.Execution.skipTestExecution
    }

    static String getApiTestsProjectDir() {
        new File(Properties.QA.repository, "dp-api-tests").absolutePath
    }

    static String getApiTestsResultsDir() {
        new File(workspace, "reports/api_tests/").absolutePath
    }

    static String getLoadTestsProjectDir() {
        new File(Properties.QA.repository, "load_tests").absolutePath
    }

    static String getLoadTestsResultsDir(LoadTestsSimulation simulation) {

        def reportDirName = simulation.toString().split("\\.").last()

        new File(workspace, "reports/load_tests/${reportDirName}").absolutePath
    }

    static boolean isDockerUsed() {
        !project.selectedInstallations.collect { installation -> installation.name.startsWith(DOCKER_INSTALLATION) }.isEmpty()
    }

    static String isStoppingPlatform() {
        profile.toLowerCase().endsWith("platformstop")
    }

    static String getProfile() {
        project.selectedProfile['name']
    }

    static boolean isProfileSelected(String profileName) {
        project.selectedProfile['name'] == profileName
    }

    static boolean isTestSelected(final String testName) {
        !project.selectedTests.findAll { test -> test['name'] == testName }.isEmpty()
    }

    static Project getProject() {
        new GradleSpaceliftDelegate().project()
    }

    static SpaceliftExtension getSpacelift() {
        project.spacelift
    }

    static File getWorkspace() {
        (File) spacelift.workspace
    }
}
