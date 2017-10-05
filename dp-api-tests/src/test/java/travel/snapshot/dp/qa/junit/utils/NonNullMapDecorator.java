package travel.snapshot.dp.qa.junit.utils;

import static org.junit.Assert.*;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;

/**
 * Map used for test entities.
 *
 * If non-existing map key is requested in a test scenario the test will fail instead of the
 * map returning null and the test failing with some nonsense exception somewhere in the dp-api-tests core code.
 */
@RequiredArgsConstructor(staticName = "of")
//@AllArgsConstructor
public class NonNullMapDecorator<K, V> {

    @NonNull private Map<K, V> map;

    public V get(K key) {
        if (!map.containsKey(key)) {
            fail("Entity " + key.toString() + " not found in test data. Check your yaml files!");
        }
        return map.get(key);
    }

    public Collection<V> values() {
        return map.values();
    }
}
