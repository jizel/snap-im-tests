package travel.snapshot.dp.qa.cucumber.helpers;

import cucumber.api.Transformer;

public class Converters {

    public static class NullEmptyStringConverter extends Transformer<String> {

        public static final String NULL_KEYWORD = "/null";

        @Override
        public String transform(String value) {
            if (NULL_KEYWORD.equals(value)) {
                return null;
            }
            return value;
        }

    }

    public static class BooleanStringConverter extends Transformer<Boolean> {

        public static final String NULL_KEYWORD = "/null";

        @Override
        public Boolean transform(String value) {
            return Boolean.parseBoolean(value);
        }

    }
}
