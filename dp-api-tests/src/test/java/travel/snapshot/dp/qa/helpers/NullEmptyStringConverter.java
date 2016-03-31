package travel.snapshot.dp.qa.helpers;

import cucumber.api.Transformer;

public class NullEmptyStringConverter extends Transformer<String> {

    public static final String NULL_KEYWORD = "/null";

    public static final String INCOMPLETE = "incomplete";

    @Override
    public String transform(String value) {
        if (NULL_KEYWORD.equals(value)) {
            return null;
        }
        else if (INCOMPLETE.equals(value)) {
            return INCOMPLETE;
        } else {
            return value;
        }
    }

}
