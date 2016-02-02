package travel.snapshot.qa.manager.api;

import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Waiting condition which satisfaction outcome solely depends on a value passed to its satisfaction method.
 */
public class BasicWaitingCondition implements ExecutionCondition<Boolean> {

    @Override
    public boolean satisfiedBy(Boolean satisfied) throws ExecutionException {
        return satisfied;
    }
}
