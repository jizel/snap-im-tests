package travel.snapshot.dp.qa.junit.utils;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMap.endpointEntityMap;

import travel.snapshot.dp.api.model.EntityDto;

import java.util.Map;
/**
 * Reversed map for EndpointEntityMap - get uri based on Dto type
 */
public class EntityEndpointMap{

    public static final Map<Class<? extends EntityDto>, String> entityEndpointMap = getEntityEndpointMap();

    private static Map<Class<? extends EntityDto>, String> getEntityEndpointMap() {
            return unmodifiableMap(endpointEntityMap
                    .entrySet()
                    .stream()
                    .collect(toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }
}
