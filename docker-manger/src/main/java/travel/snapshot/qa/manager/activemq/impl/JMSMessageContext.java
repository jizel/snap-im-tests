package travel.snapshot.qa.manager.activemq.impl;

/**
 * JMS message context is used in connection with {@link JMSHelper}.
 */
public class JMSMessageContext {

    private final int sessionAcknowledge;

    private final boolean toQueue;

    private final boolean transacted;

    private JMSMessageContext(Builder builder) {
        this.sessionAcknowledge = builder.sessionAcknowledge;
        this.toQueue = builder.toQueue;
        this.transacted = builder.transacted;
    }

    public static class Builder {

        // translates to java.jmx.Session.AUTO_ACKNOWLEDGE
        private int sessionAcknowledge = 1;

        private boolean toQueue = false;

        private boolean transacted = false;

        public Builder toQueue(boolean toQueue) {
            this.toQueue = toQueue;
            return this;
        }

        public Builder transacted(boolean transacted) {
            this.transacted = transacted;
            return this;
        }

        /**
         * @param sessionAcknowledge maps to acknowledge constants in javax.jms.Session, defauls to 1 which translates
         *                           to {@link javax.jms.Session#AUTO_ACKNOWLEDGE}
         * @return this
         * @throws IllegalArgumentException if {@code sessionAcknowledge} is lower then 0 or higher then 3.
         */
        public Builder sessionAcknowledge(int sessionAcknowledge) {

            if (sessionAcknowledge < 0 || sessionAcknowledge > 3) {
                throw new IllegalArgumentException("JMS session acknowledge has to be 0,1,2 or 3");
            }

            this.sessionAcknowledge = sessionAcknowledge;
            return this;
        }

        public JMSMessageContext build() {
            return new JMSMessageContext(this);
        }
    }

    public int getSessionAcknowledge() {
        return sessionAcknowledge;
    }

    public boolean isToQueue() {
        return toQueue;
    }

    public boolean isTransacted() {
        return transacted;
    }
}
