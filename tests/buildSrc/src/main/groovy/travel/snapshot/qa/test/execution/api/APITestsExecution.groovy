package travel.snapshot.qa.test.execution.api

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.task.os.CommandTool
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.PropertyResolver

class APITestsExecution {

    private String single

    private String apiTestsDpProperties

    APITestsExecution(final DataPlatformTestOrchestration testOrchestration) {
        this.apiTestsDpProperties = PropertyResolver.resolveApiTestsDpProperties(testOrchestration.get())
    }

    APITestsExecution single(String single) {
        this.single = single
        this
    }

    def execute() {

        CommandTool apiTestsExecution = Spacelift.task("gradle")
                .parameters("--project-dir", ProjectHelper.apiTestsProjectDir)
                .parameter("-Dapi-tests-config=${apiTestsDpProperties}")
                .parameter("-Dserenity.outputDirectory=${ProjectHelper.apiTestsResultsDir}")

        if (single) {
            apiTestsExecution.parameter("-Dtest.single=${single}")
        }

        apiTestsExecution.parameters("clean", "test", "aggregate").shouldExitWith(0, 1).execute().await()
    }
}
