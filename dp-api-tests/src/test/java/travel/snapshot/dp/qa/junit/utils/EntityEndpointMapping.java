package travel.snapshot.dp.qa.junit.utils;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointCreateDtoMap;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;

import travel.snapshot.dp.api.model.EntityDto;

import java.util.Map;
/**
 * Reversed map for EndpointEntityMapping - get uri based on Dto type
 */
public class EntityEndpointMapping {

    public static final Map<Class<? extends EntityDto>, String> entityDtoEndpointMap = revertMap(endpointDtoMap);
    public static final Map<Class<?>, String> entityCreateDtoEndpointMap = revertMap(endpointCreateDtoMap);


    private static <K,V> Map<V,K> revertMap(Map<K,V> original){
        return unmodifiableMap(original
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }
}
