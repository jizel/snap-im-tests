package travel.snapshot.dp.qa.nonpms.jms.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class FailureNotification extends Notification {

    @JsonProperty
    private Provider provider;
    @JsonProperty
    private List<String> affectedProperties;
    @JsonProperty
    private LocalDate affectedDate;

    public FailureNotification() {
        super();
    }

    @Override
    public boolean isFailure() {
        return true;
    }
}
