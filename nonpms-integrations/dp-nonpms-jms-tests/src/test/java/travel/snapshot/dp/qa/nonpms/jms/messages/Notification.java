package travel.snapshot.dp.qa.nonpms.jms.messages;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import travel.snapshot.dp.qa.nonpms.jms.util.ObjectMappers;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Notification {

    public abstract Provider getProvider();
    public abstract List<String> getAffectedProperties();
    public abstract boolean isFailure();

    public static EtlNotification etlNotification(String json) throws IOException {
        return ObjectMappers.createObjectMapper().readValue(json, EtlNotification.class);
    }

    public static FailureNotification failureNotification(String json) throws IOException {
        return ObjectMappers.createObjectMapper().readValue(json, FailureNotification.class);
    }

}
