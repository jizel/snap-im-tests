package travel.snapshot.qa.test.execution.report

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import travel.snapshot.qa.test.execution.load.LoadTestsSimulation
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.Properties

class LoadTestGuiReporter {

    static def report(LoadTestsSimulation simulation) {
        if (ProjectHelper.loadTestRunning && Properties.Execution.runningWithGui) {

            Spacelift.task(CommandTool)
                    .programName("google-chrome")
                    .parameter(new File(getLoadTestReportDirectory(simulation), "index.html").absolutePath)
                    .execute().await()
        }
    }

    private static String getLoadTestReportDirectory(LoadTestsSimulation simulation) {
        // there can be more then one load test report directory for some simulation, we return the 'latest' one
        // each target report directory has timestamp suffix
        new File(ProjectHelper.getLoadTestsResultsDir(simulation)).listFiles().sort().last().absolutePath
    }
}
