package travel.snapshot.qa.integration.jms.instagram;

import io.airlift.airline.Command;
import io.airlift.airline.Option;
import travel.snapshot.dp.integration.messages.instagram.DataPaths;
import travel.snapshot.dp.integration.messages.instagram.InstagramLoadMessage;
import travel.snapshot.dp.integration.messages.instagram.InstagramLoadMetadata;
import travel.snapshot.qa.integration.jms.JMSMessageSender;
import travel.snapshot.qa.integration.jms.MessageDestination;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Sends message to Instagram DWH Loader module
 */
@Command(name = "loader-instagram", description = "sends loading message to Instagram DwhLoader module")
public class InstagramLoaderCommand extends BaseInstagramCommand implements Runnable {

    @Option(name = {"-m", "--media-data-path"}, description = "media data path", arity = 1, required = true)
    private String mediaDataPath;

    @Option(name = {"-g", "--tag-data-path"}, description = "tag data path", arity = 1, required = true)
    private String tagDataPath;

    @Option(name = {"-u", "--user-data-path"}, description = "user data path", arity = 1, required = true)
    private String userDataPath;

    @Option(name = {"--download-min-timestamp"}, description = "When not set, it equals 30 days before now",
            arity = 1, required = false)
    private String downloadMinTimeStamp = LocalDateTime.now().minusDays(30).toString();

    @Option(name = {"--download-max-timestap"}, description = "When not set, it equals to now.",
            arity = 1, required = false)
    private String downloadMaxTimeStamp = Instant.now().toString();

    @Override
    public void run() {

        super.validate();
        this.validate();

        final JMSMessageSender messenger = new JMSMessageSender(getJMSHelper().toQueue(MessageDestination.INSTAGRAM_LOADER.toQueue()));

        final InstagramLoadMetadata loadMetadata = InstagramLoadMetadata.builder()
                .downloadMinTimeStamp(getInstant(downloadMinTimeStamp))
                .downloadMaxTimeStamp(getInstant(downloadMaxTimeStamp))
                .propertyId(propertyIdUUID)
                .propertyTimeZone(propertyTimeZone)
                .tagName(tagName)
                .build();

        final DataPaths dataPaths = DataPaths.builder()
                .mediaDataPath(mediaDataPath)
                .tagDataPath(tagDataPath)
                .userDataPath(userDataPath)
                .build();

        final InstagramLoadMessage loadMessage = new InstagramLoadMessage(integrationIdUUID, loadMetadata, dataPaths);

        messenger.send(loadMessage, MessageDestination.INSTAGRAM_LOADER.resolve(destination));
    }

    private Instant getInstant(String timestamp) {
        Instant instant = null;

        try {
            instant = Instant.parse(timestamp);
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("Unable to parse Instant from: %s", timestamp));
        }

        return instant;
    }
}
