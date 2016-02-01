package travel.snapshot.qa.manager.mongodb.api;

public class MongoDBManagerException extends RuntimeException {

    public MongoDBManagerException() {
    }

    public MongoDBManagerException(String message) {
        super(message);
    }

    public MongoDBManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoDBManagerException(Throwable cause) {
        super(cause);
    }
}
