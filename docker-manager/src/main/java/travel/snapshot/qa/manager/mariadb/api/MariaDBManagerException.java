package travel.snapshot.qa.manager.mariadb.api;

public class MariaDBManagerException extends RuntimeException {

    public MariaDBManagerException() {
        super();
    }

    public MariaDBManagerException(String message) {
        super(message);
    }

    public MariaDBManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MariaDBManagerException(Throwable cause) {
        super(cause);
    }
}
