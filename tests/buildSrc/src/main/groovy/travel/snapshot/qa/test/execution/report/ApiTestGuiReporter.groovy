package travel.snapshot.qa.test.execution.report

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.PropertyResolver

/**
 * After some API tests are done, this opens Serenity test report in Google Chrome.
 */
class ApiTestGuiReporter {

    static def report() {
        if (ProjectHelper.apiTestRunning && PropertyResolver.runningWithGui) {
            Spacelift.task(CommandTool)
                    .programName("google-chrome")
                    .parameter(new File(ProjectHelper.apiTestsResultsDir, "index.html").absolutePath)
                    .execute().await()
        }
    }

}
