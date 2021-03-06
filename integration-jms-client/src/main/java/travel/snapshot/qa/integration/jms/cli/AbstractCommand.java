package travel.snapshot.qa.integration.jms.cli;

import io.airlift.airline.Option;
import travel.snapshot.qa.integration.jms.JMSConnectionHelper;

/**
 * Encapsulates options which are same for all commands.
 */
public abstract class AbstractCommand implements Runnable {

    @Option(name = {"-b", "--broker-url"}, title = "url",
            description = "url of JMS broker to send a message to, defaults to tcp://localhost:61616", arity = 1)
    protected String brokerURL = "tcp://localhost:61616";

    @Option(name = {"-d", "--destination"}, title = "destination", description = "destination to send a message to. " +
            "It is not needed when you are ok with defaults for some particular topic or queue.",
            arity = 1)
    protected String destination;

    @Option(name = {"-t", "--transacted"}, title = "transacted",
            description = "turns on transaction mode, defaults to false", arity = 0)
    protected boolean transacted;

    /**
     * AUTO_ACKNOWLEDGE = 1 CLIENT_ACKNOWLEDGE = 2 DUPS_OK_ACKNOWLEDGE = 3 SESSION_TRANSACTED = 0
     */
    @Option(name = {"-s", "--sessionAcknowledge"}, title = "sessionAcknowledge",
            description = "mode of session acknowledge, from 0 to 3, defauls to 1. " +
                    "This reflects to java.jmx.Session acknowledge constants. Defaults to " +
                    "java.jmx.Session.AUTO_ACKNOWLEDGE which is 1.", arity = 1)
    protected int sessionAcknowledge = 1;

    public JMSConnectionHelper getJMSHelper() {
        return new JMSConnectionHelper().brokerUrl(brokerURL).sessionAcknowledge(sessionAcknowledge).transacted(transacted);
    }
}
