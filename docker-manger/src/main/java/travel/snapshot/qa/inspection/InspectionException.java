package travel.snapshot.qa.inspection;

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
