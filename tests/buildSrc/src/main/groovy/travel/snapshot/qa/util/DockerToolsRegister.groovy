package travel.snapshot.qa.util

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.CommandBuilder
import org.arquillian.spacelift.process.ProcessInteractionBuilder
import org.arquillian.spacelift.task.Task
import org.arquillian.spacelift.task.TaskFactory
import org.arquillian.spacelift.task.TaskRegistry
import org.arquillian.spacelift.task.os.CommandTool

/**
 * Registers Spacelift tasks into the register so they can be later retrieved from the register and used,
 * referencing just its alias. There are environment properties set as well for such task.
 */
class DockerToolsRegister {

    /**
     * Registers tools to provided registry. Environment properties will not be set.
     *
     * @param registry register to register tools / tasks into
     */
    def register(TaskRegistry registry) {
        register(registry, null)
    }

    /**
     * Registers tools into the specified registry with provided properties. There are two tools registered -
     * 'docker' and 'docker-machine'.
     *
     * @param registry register to register tools / tasks into
     * @param properties properties to pass to tools before registering them into the registry
     * @return
     */
    def register(TaskRegistry registry, Map properties) {

        registry.register(DockerTool, new TaskFactory() {

            @Override
            DockerTool create() {
                Task task = new DockerTool()
                ((DockerTool) task).setEnvProperties(properties)
                task.executionService = Spacelift.service()
                task
            }

            @Override
            Collection<String> aliases() {
                ["docker"]
            }
        })

        registry.register(DockerMachineTool, new TaskFactory() {

            @Override
            Task create() {
                Task task = new DockerMachineTool()
                task.executionService = Spacelift.service()
                task
            }

            @Override
            Collection<String> aliases() {
                ["docker-machine"]
            }
        })
    }

    class DockerTool extends CommandTool {

        DockerTool() {
            super()

            this.commandBuilder = new CommandBuilder("docker")

            this.interaction = new ProcessInteractionBuilder()
                    .outputPrefix("docker: ")
                    .when("(?s).*")
                    .printToOut()
                    .build()
        }

        DockerTool setEnvProperties(Map properties) {
            if (properties) {
                super.environment.putAll(properties)
            }
            this
        }

        @Override
        String toString() {
            "DockerTool (${commandBuilder})"
        }
    }

    class DockerMachineTool extends CommandTool {

        DockerMachineTool() {
            super()

            this.commandBuilder = new CommandBuilder("docker-machine")

            this.interaction = new ProcessInteractionBuilder()
                    .outputPrefix("docker-machine: ")
                    .when("(?s).*")
                    .printToOut()
                    .build()
        }

        DockerMachineTool setEnvProperties(Map properties) {
            if (properties) {
                super.environment.putAll(properties)
            }
            this
        }

        @Override
        String toString() {
            "DockerMachineTool (${commandBuilder})"
        }
    }
}
