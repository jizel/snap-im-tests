package travel.snapshot.qa.manager.activemq.api;

import travel.snapshot.qa.manager.api.container.ContainerManagerException;

public class ActiveMQManagerException extends ContainerManagerException {

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
