package travel.snapshot.qa.manager.tomcat.api;

public class DeploymentStateParserException extends Exception {

    private static final long serialVersionUID = 2366572219517921713L;

    public DeploymentStateParserException() {
        super();
    }

    public DeploymentStateParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentStateParserException(String message) {
        super(message);
    }

    public DeploymentStateParserException(Throwable cause) {
        super(cause);
    }

}
