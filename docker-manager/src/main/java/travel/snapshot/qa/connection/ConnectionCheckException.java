package travel.snapshot.qa.connection;

public class ConnectionCheckException extends RuntimeException {

    public ConnectionCheckException() {
        super();
    }

    public ConnectionCheckException(String message) {
        super(message);
    }

    public ConnectionCheckException(Throwable cause) {
        super(cause);
    }

    public ConnectionCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
