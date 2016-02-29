package travel.snapshot.qa.test.execution.api

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.task.os.CommandTool
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.util.PropertyResolver

class APITestsExecution {

    private final DataPlatformTestOrchestration testOrchestration

    private final File workspace

    private String single

    APITestsExecution(final DataPlatformTestOrchestration testOrchestration) {
        this.testOrchestration = testOrchestration
        this.workspace = new GradleSpaceliftDelegate().project().spacelift.workspace
    }

    APITestsExecution single(String single) {
        this.single = single
        this
    }

    def execute() {

        CommandTool tool = Spacelift.task("gradle")
                .parameters("--project-dir", projectDir)
                .parameter("-Dapi-tests-config=${apiTestsProperties}")

        if (single) {
            tool.parameter("-Dtest.single=${single}")
        }

        tool.parameter("test").shouldExitWith(0, 1).execute().await()

        Spacelift.task("gradle").parameters("--project-dir", projectDir, "aggregate").execute().await()
    }

    private def getProjectDir() {
        new File(workspace, "dataplatformqa/dp-api-tests").absolutePath
    }

    private def getApiTestsProperties() {
        PropertyResolver.resolveApiTestsDpProperties(testOrchestration.get())
    }
}
