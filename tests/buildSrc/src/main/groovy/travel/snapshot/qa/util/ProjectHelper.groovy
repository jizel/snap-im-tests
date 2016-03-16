package travel.snapshot.qa.util

import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.SpaceliftExtension
import org.gradle.api.Project
import travel.snapshot.qa.test.execution.load.LoadTestsSimulation

final class ProjectHelper {

    static boolean isLoadTestRunning() {
        !project.selectedTests.findAll { test -> test['name'] == "loadTests" }.isEmpty()
    }

    static boolean isApiTestRunning() {
        !project.selectedTests.findAll { test -> test['name'].startsWith("apiTests") }.isEmpty()
    }

    static String getApiTestsProjectDir() {
        new File(workspace, "dataplatformqa/dp-api-tests").absolutePath
    }

    static String getApiTestsResultsDir() {
        new File(workspace, "reports/api_tests/").absolutePath
    }

    static String getLoadTestsProjectDir() {
        new File(workspace, "dataplatformqa/load_tests").absolutePath
    }

    static String getLoadTestsResultsDir(LoadTestsSimulation simulation) {

        def reportDirName = simulation.toString().split("\\.").last()

        new File(workspace, "reports/load_tests/${reportDirName}").absolutePath
    }

    static boolean isDockerUsed() {
        !project.selectedInstallations.collect { installation -> installation.name.startsWith('docker') }.isEmpty()
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
