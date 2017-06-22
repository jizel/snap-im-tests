package travel.snapshot.dp.qa.cucumber.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.util.RawValue;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author konkol
 */
public enum FieldType {

    JSON {
        @Override
        public JsonNode getJsonNode(String value) {
            return factory.rawValueNode(new RawValue(value));
        }
    },
    STRING,
    INTEGER,
    BOOL {
        @Override
        public JsonNode getJsonNode(String value) {
            return factory.booleanNode(Boolean.parseBoolean(value));
        }

        @Override
        public String getFilterValue(String source) {
            return "true".equalsIgnoreCase(source) ? "1" : "0";
        }

    };

    private static final JsonNodeFactory factory = new JsonNodeFactory(false);

    /**
     * Returns field type for provided type string (case ignored).
     *
     * @param type textual description of field type
     * @return matching FieldType instance
     * @throws NoSuchElementException when no matching type is found
     */
    public static FieldType getType(String type) {
        Optional<FieldType> result = Arrays.stream(values())
                .filter((f) -> f.match(type))
                .findFirst();
        return result.get();
    }

    public JsonNode getJsonNode(String value) {
        return factory.textNode(value);
    }

    public String getFilterValue(String source) {
        return "'" + source + "'";
    }

    protected boolean match(String type) {
        return name().equalsIgnoreCase(type);
    }

}
