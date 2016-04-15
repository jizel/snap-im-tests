package travel.snapshot.qa.manager.jboss.check;

import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;

public class JBossStartedCondition implements ExecutionCondition<Boolean> {

    @Override
    public boolean satisfiedBy(Boolean started) throws ExecutionException {
        return started;
    }
}
