package travel.snapshot.qa.integration.jms.cli;

import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import travel.snapshot.qa.integration.jms.instagram.InstagramGatheringCommand;
import travel.snapshot.qa.integration.jms.instagram.InstagramLoaderCommand;
import travel.snapshot.qa.integration.jms.instagram.InstagramSchedulingCommand;
import io.airlift.airline.Help;

/**
 * Entry point to the client.
 */
public class JmsCli {

    public static void main(String[] args) {
        CliBuilder<Runnable> builder = Cli.<Runnable>builder("jmscli").withDefaultCommand(Help.class)
                .withCommand(Help.class)
                .withCommand(InstagramSchedulingCommand.class)
                .withCommand(InstagramGatheringCommand.class)
                .withCommand(InstagramLoaderCommand.class);

        builder.build().parse(args).run();
    }

}
