package travel.snapshot.qa.manager.activemq.api;

public class ActiveMQManagerException extends RuntimeException {

    public ActiveMQManagerException() {
        super();
    }

    public ActiveMQManagerException(String message) {
        super(message);
    }

    public ActiveMQManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActiveMQManagerException(Throwable cause) {
        super(cause);
    }
}
