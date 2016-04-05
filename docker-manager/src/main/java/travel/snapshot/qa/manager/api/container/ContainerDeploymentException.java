package travel.snapshot.qa.manager.api.container;

public class ContainerDeploymentException extends ContainerManagerException {

    private static final long serialVersionUID = -7127228904651848111L;

    public ContainerDeploymentException() {
        super();
    }

    public ContainerDeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerDeploymentException(String message) {
        super(message);
    }

    public ContainerDeploymentException(Throwable cause) {
        super(cause);
    }
}
