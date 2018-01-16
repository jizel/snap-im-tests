package travel.snapshot.dp.qa.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import travel.snapshot.dp.api.type.SalesforceId;

import java.io.IOException;

/**
 * Created by zelezny on 3/28/2017.
 */
public final class SalesforceIdStdSerializer extends StdSerializer<SalesforceId> {

    public SalesforceIdStdSerializer() {
        super(SalesforceId.class);
    }

    @Override
    public void serialize(SalesforceId value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.toString());
    }

}
