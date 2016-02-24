package travel.snapshot.dp.qa.helpers;

import cucumber.api.Transformer;

public class ClassStringConverter extends Transformer<Class<?>> {

    @Override
    public Class<?> transform(String value) {
        switch (value) {
            case "int":
                return Integer.class;
            case "double":
                return Double.class;
            case "long":
                return Long.class;
            case "string":
                return String.class;
            default:
                throw new IllegalArgumentException(String.format("Unable to convert %s to respective class.", value));
        }
    }
}
