package travel.snapshot.qa.integration.jms.instagram;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import travel.snapshot.dp.integration.messages.instagram.InstagramGatheringMessage;
import travel.snapshot.qa.integration.jms.JMSMessageSender;
import travel.snapshot.qa.integration.jms.MessageDestination;

/**
 * Sends gathering message from Starter to Data gatherer.
 */
@Command(name = "gathering-instagram", description = "sends gathering message to Instagram Gathering module from Starter")
public class InstagramGatheringCommand extends BaseInstagramCommand implements Runnable {

    @Option(name = {"-a", "--access-token"}, description = "access token for Instagram", arity = 1, required = true)
    private String accessToken;

    @Override
    public void run() {

        validate();

        final JMSMessageSender messenger = new JMSMessageSender(getJMSHelper().toQueue(MessageDestination.INSTAGRAM_GATHERING.toQueue()));

        final InstagramGatheringMessage gatheringMessage = InstagramGatheringMessage.builder()
                .accessToken(accessToken)
                .integrationId(integrationIdUUID)
                .propertyId(propertyIdUUID)
                .propertyTimeZone(propertyTimeZone)
                .tagName(tagName)
                .build();

        messenger.send(gatheringMessage, MessageDestination.INSTAGRAM_GATHERING.resolve(destination));
    }

}