package travel.snapshot.dp.qa.easyTests.utils;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;

/**
 * Map used for test entities. If non-existing map key is requested in a test scenario the test will fail instead of the map returning null
 * and the test failing with some nonsense exception somewhere in the dp-api-tests core code.
 */
public class EntityNonNullMap<K,V> extends LinkedHashMap<K,V> {

    public EntityNonNullMap(LinkedHashMap<K,V> linkedHashMap) {
        super(linkedHashMap);
    }

    @Override
    public V get(Object key) {
        if(super.get(key) == null){
            fail("Entity " + key.toString() + " not found in test data. Check your yaml files!");
        }
        return super.get(key);
    }
}
