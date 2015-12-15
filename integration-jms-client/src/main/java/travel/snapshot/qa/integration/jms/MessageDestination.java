package travel.snapshot.qa.integration.jms;

/**
 * Encapsulates possible topic / queue destinations.
 */
public enum MessageDestination {

    INSTAGRAM_SCHEDULING("instagram.start.topic", false),
    INSTAGRAM_GATHERING("integration.instagram.gather", true),
    INSTAGRAM_LOADER("integration.instagram.load", true);

    private final String name;

    private final boolean toQueue;

    private MessageDestination(String name, boolean toQueue) {
        this.name = name;
        this.toQueue = toQueue;
    }

    /**
     * Decides if a destination will be the one provided by default or one provided in {@code customDestination}. Custom
     * destination is used when it is not a null object and it is not an empty String.
     *
     * @param customDestination destination which overrides the default one for the given enumeration
     * @return {@code customDestination} when not null or empty
     */
    public String resolve(String customDestination) {
        if (customDestination == null || customDestination.isEmpty()) {
            return name;
        }

        return customDestination;
    }

    /**
     * @return true if message will be sent to queue, false otherwise
     */
    public boolean toQueue() {
        return toQueue;
    }
}
