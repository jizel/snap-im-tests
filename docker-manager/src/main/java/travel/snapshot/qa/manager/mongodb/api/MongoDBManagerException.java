package travel.snapshot.qa.manager.mongodb.api;

import travel.snapshot.qa.manager.api.container.ContainerManagerException;

public class MongoDBManagerException extends ContainerManagerException {

    public MongoDBManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
