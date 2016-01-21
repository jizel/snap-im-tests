package travel.snapshot.qa.manager.tomcat.check;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.configuration.Validate;

/**
 * Checks if a Tomcat container is started or not.
 *
 * @see TomcatStartedCheckTask
 */
public class TomcatStartChecker extends Task<TomcatManagerConfiguration, Boolean> {

    public static final ExecutionCondition<Boolean> TOMCAT_STARTED_CONDITION =
            new TomcatStartChecker.TomcatStartedCondition();

    @Override
    protected Boolean process(TomcatManagerConfiguration configuration) throws Exception {
        Validate.notNull(configuration, "Configuration must not be a null object.");
        return Spacelift.task(configuration, TomcatStartedCheckTask.class).execute().await();
    }

    private static final class TomcatStartedCondition implements ExecutionCondition<Boolean> {

        @Override
        public boolean satisfiedBy(Boolean started) throws ExecutionException {
            return started;
        }
    }
}
