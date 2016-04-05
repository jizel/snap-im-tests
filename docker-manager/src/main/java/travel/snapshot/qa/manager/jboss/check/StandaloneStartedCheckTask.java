package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.spacelift.JBossCLI;
import travel.snapshot.qa.manager.jboss.spacelift.JBossCLI.NotExecutableScriptException;

import java.io.FileNotFoundException;

/**
 * Checks start of single server instance, started in standalone mode.
 */
public class StandaloneStartedCheckTask extends Task<JBossManagerConfiguration, Boolean> {

    @Override
    protected Boolean process(JBossManagerConfiguration configuration) throws Exception {

        if (configuration == null) {
            throw new IllegalStateException("configuration is a null object");
        }

        configuration.validate();

        ProcessResult processResult = null;

        try {
            processResult = Spacelift.task(JBossCLI.class)
                    .environment("JBOSS_HOME", configuration.getJBossHome())
                    .user(configuration.getUser())
                    .password(configuration.getPassword())
                    .connect()
                    .cliCommand(":read-attribute(name=server-state)")
                    .execute().await();
        } catch (Exception ex) {
            if (ex.getCause() instanceof FileNotFoundException) {
                throw ex;
            } else if (ex.getCause() instanceof NotExecutableScriptException) {
                throw ex;
            }
        }

        if (processResult == null || processResult.exitValue() != 0) {
            return false;
        }

        boolean success = false;
        boolean running = false;

        for (String output : processResult.output()) {

            output = output.toLowerCase();

            if (output.contains("result") && output.contains("running")) {
                running = true;
                continue;
            }

            if (output.contains("outcome") && output.contains("success")) {
                success = true;
            }
        }

        return success && running;
    }

}
