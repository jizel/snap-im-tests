package travel.snapshot.qa.manager.api.container;

public class ContainerManagerException extends RuntimeException {

    private static final long serialVersionUID = 7843187564646025565L;

    public ContainerManagerException() {
        super();
    }

    public ContainerManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerManagerException(String message) {
        super(message);
    }

    public ContainerManagerException(Throwable cause) {
        super(cause);
    }
}
