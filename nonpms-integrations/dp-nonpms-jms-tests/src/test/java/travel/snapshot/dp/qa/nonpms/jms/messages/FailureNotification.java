package travel.snapshot.dp.qa.nonpms.jms.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class FailureNotification extends Notification {

    @JsonProperty
    Provider provider;
    @JsonProperty
    LocalDate affectedDate;
    @JsonProperty
    List<String> affectedProperties;
    @JsonProperty
    ErrorType errorType;
    @JsonProperty
    String integrationError;
    @JsonProperty
    ZonedDateTime timestamp;

    public FailureNotification() {
        super();
    }

    @Override
    public boolean isFailure() {
        return true;
    }

    public enum ErrorType {
        SERVICE_DOWN,
        AUTHORIZATION_PROBLEM,
        UNEXPECTED_ERROR
    }

}
