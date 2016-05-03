package travel.snapshot.qa.manager.mariadb.api;

import travel.snapshot.qa.manager.api.container.ContainerManagerException;

public class MariaDBManagerException extends ContainerManagerException {

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
