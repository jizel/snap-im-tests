package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.spacelift.JBossCLI;
import travel.snapshot.qa.manager.jboss.spacelift.JBossCLI.NotExecutableScriptException;

import java.io.FileNotFoundException;

/**
 * Checks STARTED status of slave server in a domain.
 */
public class ServerInDomainStartCheckTask extends Task<String, Boolean> {

    private JBossManagerConfiguration configuration = new JBossManagerConfiguration();

    public ServerInDomainStartCheckTask configuration(JBossManagerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
        return this;
    }

    @Override
    protected Boolean process(String domainServer) throws Exception {

        ProcessResult result;

        configuration.validate();

        String command = "/host=" + configuration.getDomainMasterHostName()
                + "/server-config=" + domainServer
                + ":read-resource(include-runtime=true)";

        try {
            result = Spacelift.task(JBossCLI.class)
                    .environment("JBOSS_HOME", configuration.getJBossHome())
                    .user(configuration.getUser())
                    .password(configuration.getPassword())
                    .connect()
                    .cliCommand(command)
                    .execute()
                    .await();

            for (String line : result.output()) {
                if (line.contains("status") && line.contains("STARTED")) {
                    return true;
                }
            }

        } catch (ExecutionException ex) {
            if (ex.getCause() instanceof FileNotFoundException) {
                throw ex;
            } else if (ex.getCause() instanceof NotExecutableScriptException) {
                throw ex;
            }
        }

        return false;
    }

}
