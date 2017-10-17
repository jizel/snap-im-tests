package travel.snapshot.dp.qa.jms.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class EtlNotification extends Notification {

    @JsonProperty
    private Provider provider;
    @JsonProperty
    private List<DateRange> affectedDateRanges;
    @JsonProperty
    private List<String> affectedProperties;
    @JsonProperty
    private ZonedDateTime timestamp;

    public EtlNotification() {
        super();
    }

    @Override
    public boolean isFailure() {
        return false;
    }

}
