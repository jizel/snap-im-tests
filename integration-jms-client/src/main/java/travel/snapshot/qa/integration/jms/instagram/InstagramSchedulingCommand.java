package travel.snapshot.qa.integration.jms.instagram;

import java.time.Instant;

import io.airlift.airline.Command;
import travel.snapshot.dp.integration.messages.common.SchedulerMessage;
import travel.snapshot.qa.integration.jms.JMSMessageSender;
import travel.snapshot.qa.integration.jms.MessageDestination;
import travel.snapshot.qa.integration.jms.cli.AbstractCommand;

/**
 * Sends scheduling message to Instagram Starter module.
 */
@Command(name = "scheduling-instagram", description = "sends scheduling message to Instagram Starter module")
public class InstagramSchedulingCommand extends AbstractCommand implements Runnable {

    @Override
    public void run() {

        final JMSMessageSender messanger = new JMSMessageSender(getJMSHelper());

        final SchedulerMessage schedulerMessage = new SchedulerMessage(Instant.now());

        messanger.send(schedulerMessage, MessageDestination.INSTAGRAM_SCHEDULING.resolve(destination));
    }

}
