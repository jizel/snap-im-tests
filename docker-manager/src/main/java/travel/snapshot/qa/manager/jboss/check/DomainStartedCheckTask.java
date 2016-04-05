package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.spacelift.JBossCLI;
import travel.snapshot.qa.manager.jboss.spacelift.JBossCLI.NotExecutableScriptException;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

/**
 * Checks if domain has started, meaning all slave servers are being about to start.
 */
public class DomainStartedCheckTask extends Task<Object, Boolean> {

    private JBossManagerConfiguration configuration = new JBossManagerConfiguration();

    public DomainStartedCheckTask configuration(JBossManagerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }

        return this;
    }

    @Override
    protected Boolean process(Object input) throws Exception {

        ProcessResult domainServersStartInitiatedResult;

        configuration.validate();

        try {
            domainServersStartInitiatedResult = Spacelift.task(JBossCLI.class)
                    .environment("JBOSS_HOME", configuration.getJBossHome())
                    .user(configuration.getUser())
                    .password(configuration.getPassword())
                    .connect()
                    .cliCommand("/host=" + configuration.getDomainMasterHostName() + ":read-children-names(child-type=server)")
                    .execute()
                    .reexecuteEvery(5, TimeUnit.SECONDS)
                    .until(configuration.getDomainStartTimeout(), TimeUnit.SECONDS, result -> {

                        int runningDomainServers = 0;

                        boolean success = false;

                        for (String line : result.output()) {
                            if (line.contains("outcome") && line.contains("success")) {
                                success = true;
                                break;
                            }
                        }

                        if (!success) {
                            return false;
                        }

                        for (String domainServer : configuration.getDomainServers()) {
                            for (String line : result.output()) {
                                if (line.contains(domainServer)) {
                                    ++runningDomainServers;
                                }
                            }
                        }

                        return runningDomainServers == configuration.getDomainServers().size();
                    });
        } catch (Exception ex) {
            if (ex.getCause() instanceof FileNotFoundException) {
                throw ex;
            } else if (ex.getCause() instanceof NotExecutableScriptException) {
                throw ex;
            }
            domainServersStartInitiatedResult = null;
        }

        return domainServersStartInitiatedResult != null && domainServersStartInitiatedResult.exitValue() == 0;
    }

}
