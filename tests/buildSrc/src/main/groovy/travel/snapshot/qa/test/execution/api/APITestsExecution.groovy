package travel.snapshot.qa.test.execution.api

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import travel.snapshot.qa.util.ProjectHelper

class APITestsExecution {

    private String single

    APITestsExecution() {}

    APITestsExecution(String single) {
        this.single = single
    }

    def execute(String single) {

        CommandTool apiTestsExecution = Spacelift.task("gradle")
                .parameters("--project-dir", ProjectHelper.apiTestsProjectDir)
                .parameter("-Dapi-tests-config=${ApiTestsPropertiesResolver.resolve()}")
                .parameter("-Dserenity.outputDirectory=${ProjectHelper.apiTestsResultsDir}")

        if (single) {
            apiTestsExecution.parameter("-Dtest.single=${single}")
        }

        apiTestsExecution.parameters("clean", "test", "aggregate").shouldExitWith(0, 1).execute().await()
    }

    def execute() {
        execute(single)
    }
}
