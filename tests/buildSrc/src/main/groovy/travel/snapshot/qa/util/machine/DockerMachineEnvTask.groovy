package travel.snapshot.qa.util.machine

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.execution.ExecutionCondition
import org.arquillian.spacelift.execution.ExecutionException
import org.arquillian.spacelift.process.ProcessResult
import org.arquillian.spacelift.task.Task

import java.util.concurrent.TimeUnit

/**
 * When we start some Docker machine with 'docker-machine start machineName', even starting
 * command has exited successfully, it is not possible to connect to it with subsequent docker-machine env
 * command.
 *
 * Because of this, we are repeatedly trying to successfully execute such command until it exits with status code 0.
 */
class DockerMachineEnvTask extends Task<String, List<String>> {

    @Override
    protected List<String> process(String machineName) throws Exception {
        Spacelift.task("docker-machine")
                .parameters("env", machineName)
                .shouldExitWith(0, 1)
                .execute()
                .reexecuteEvery(10, TimeUnit.SECONDS)
                .until(60, TimeUnit.SECONDS, new ExecutionCondition<ProcessResult>() {
            @Override
            public boolean satisfiedBy(ProcessResult result) throws ExecutionException {
                return result.exitValue() == 0;
            }
        }).output()
    }
}
