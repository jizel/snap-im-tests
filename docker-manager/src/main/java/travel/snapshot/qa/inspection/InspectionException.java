package travel.snapshot.qa.inspection;

/**
 * Exception thrown in case some error has occured while inspecting Docker container.
 */
public class InspectionException extends RuntimeException {

    public InspectionException() {
    }

    public InspectionException(String message) {
        super(message);
    }

    public InspectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InspectionException(Throwable cause) {
        super(cause);
    }
}
