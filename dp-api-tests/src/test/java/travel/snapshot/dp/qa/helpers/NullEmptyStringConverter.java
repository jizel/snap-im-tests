package travel.snapshot.dp.qa.helpers;

import cucumber.api.Transformer;

/**
 * Created by sedlacek on 11/3/2015.
 */
public class NullEmptyStringConverter extends Transformer<String> {


    private static final String NULL_KEYWORD = "/null";

    @Override
    public String transform(String value) {
        if (NULL_KEYWORD.equals(value)) {
            return null;
        } else {
            return value;
        }
    }

}
