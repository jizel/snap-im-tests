package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

public class JBossStartChecker extends Task<JBossManagerConfiguration, Boolean> {

    public static final ExecutionCondition<Boolean> jbossStartedCondition = new JBossStartChecker.JBossStartedCondition();

    @Override
    protected Boolean process(JBossManagerConfiguration configuration) throws Exception {

        if (configuration == null) {
            throw new IllegalStateException("Configuration must not be a null object!");
        }

        configuration.validate();

        return configuration.isDomain() ? startDomainCheck(configuration) : startStandaloneCheck(configuration);
    }

    private Boolean startStandaloneCheck(final JBossManagerConfiguration configuration) {
        return Spacelift.task(configuration, StandaloneStartedCheckTask.class).execute().await();
    }

    private Boolean startDomainCheck(JBossManagerConfiguration configuration) {

        boolean domainStarted = Spacelift.task(DomainStartedCheckTask.class).configuration(configuration).execute().await();

        if (!domainStarted) {
            return false;
        }

        // at this point we have all servers up but they are starting underneath
        // we have to check the status of every server until all are in STARTED status

        for (String domainServer : configuration.getDomainServers()) {

            boolean domainServerStarted = Spacelift.task(domainServer, ServerInDomainStartCheckTask.class)
                    .configuration(configuration)
                    .execute()
                    .await();

            if (!domainServerStarted) {
                return false;
            }
        }

        return true;
    }

    private static final class JBossStartedCondition implements ExecutionCondition<Boolean> {

        @Override
        public boolean satisfiedBy(Boolean started) throws ExecutionException {
            return started;
        }
    }

}
