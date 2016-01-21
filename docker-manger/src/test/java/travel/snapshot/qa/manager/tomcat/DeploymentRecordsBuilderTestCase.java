package travel.snapshot.qa.manager.tomcat;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecord;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecordsBuilderException;
import travel.snapshot.qa.manager.tomcat.api.DeploymentStateParserException;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseBody;

@RunWith(JUnit4.class)
public class DeploymentRecordsBuilderTestCase {

    private static final String NON_MATCHABLE_DEPLOYMENT_LINE = "nonsense";

    private static final String NON_PARSABLE_DEPLOYMENT_LINE = "a:rUnnIng:a:deployment";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void deploymentRecordBuilderTest() throws Exception {
        final TomcatResponseBody responseBody = new TomcatResponseBody();
        responseBody.addLine("/deployment:running:1:deployment");

        DeploymentRecord.DeploymentRecordsBuilder builder = new DeploymentRecord.DeploymentRecordsBuilder(responseBody);

        builder.build();
    }

    @Test
    public void nonMatchableDeploymentRecordBuilderTest() throws Exception {

        expectedException.expect(DeploymentRecordsBuilderException.class);
        expectedException.expectMessage(
                String.format("Deployment line '%s' does not match the matcher.", NON_MATCHABLE_DEPLOYMENT_LINE));

        final TomcatResponseBody responseBody = new TomcatResponseBody();
        responseBody.addLine(NON_MATCHABLE_DEPLOYMENT_LINE);

        final DeploymentRecord.DeploymentRecordsBuilder builder = new DeploymentRecord.DeploymentRecordsBuilder(responseBody);

        builder.build();
    }

    @Test
    public void invalidDeploymentRecordBuilderTest() throws Exception {

        expectedException.expect(DeploymentRecordsBuilderException.class);
        expectedException.expectCause(is(instanceOf(DeploymentStateParserException.class)));

        final TomcatResponseBody responseBody = new TomcatResponseBody();
        responseBody.addLine(NON_PARSABLE_DEPLOYMENT_LINE);

        final DeploymentRecord.DeploymentRecordsBuilder builder = new DeploymentRecord.DeploymentRecordsBuilder(responseBody);

        builder.build();
    }
}
