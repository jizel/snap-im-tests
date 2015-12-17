package travel.snapshot.qa.integration.jms.instagram;

import io.airlift.airline.Option;
import travel.snapshot.qa.integration.jms.cli.AbstractCommand;

import java.util.Arrays;
import java.util.TimeZone;
import java.util.UUID;

public abstract class BaseInstagramCommand extends AbstractCommand {

    @Option(name = {"-i", "--integration-id"}, description = "integration id", arity = 1, required = true)
    protected String integrationId;

    @Option(name = {"-p", "--property-id"}, description = "property id", arity = 1, required = true)
    protected String propertyId;

    @Option(name = {"-z", "--time-zone"}, description = "timezone of a property, defaults to GMT. Accepts all " +
            "what is in TimeZone.getAvailableIDs()", arity = 1, required = false)
    protected String propertyTimeZone = "GMT";

    @Option(name = {"-n", "--tag-name"}, description = "tag name", arity = 1, required = true)
    protected String tagName;

    protected UUID integrationIdUUID;

    protected UUID propertyIdUUID;

    protected void validate() {
        try {
            integrationIdUUID = UUID.fromString(integrationId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Integration ID UUID is in a bad format.");
        }

        try {
            propertyIdUUID = UUID.fromString(propertyId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Property ID UUID is in a bad foramt.");
        }

        if (!Arrays.asList(TimeZone.getAvailableIDs()).contains(propertyTimeZone)) {
            throw new IllegalArgumentException(String.format("Property time zone %s is not valid timezone.", propertyTimeZone));
        }
    }
}
