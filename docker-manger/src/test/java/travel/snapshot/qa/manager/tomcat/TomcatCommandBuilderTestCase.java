package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.process.Command;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.impl.TomcatCommandBuilder;

@Category(UnitTest.class)
public class TomcatCommandBuilderTestCase {

    @Test
    public void testTomcatCommandBuilder() {
        TomcatCommandBuilder builder = new TomcatCommandBuilder();

        Command builtCommand = builder.build(new TomcatManagerConfiguration.Builder().build());

        System.out.println(builtCommand.toString());
    }
}
