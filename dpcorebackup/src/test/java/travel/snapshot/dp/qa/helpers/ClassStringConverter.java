package travel.snapshot.dp.qa.helpers;

import cucumber.api.Transformer;
import travel.snapshot.dp.api.analytics.model.SingleStatsDto;
import travel.snapshot.dp.api.webperformance.model.PeriodAverageStatsDto;

public class ClassStringConverter extends Transformer<Class<?>> {

    @Override
    public Class<?> transform(String value) {
        switch (value.toLowerCase()) {
            case "int":
                return Integer.class;
            case "double":
                return Double.class;
            case "long":
                return Long.class;
            case "string":
                return String.class;
            case "boolean":
                return Boolean.class;
            case "singlestatsdto":
                return SingleStatsDto.class;
            case "periodaveragestatsdto":
                return PeriodAverageStatsDto.class;
            default:
                throw new IllegalArgumentException(String.format("Unable to convert %s to respective class.", value));
        }
    }
}
