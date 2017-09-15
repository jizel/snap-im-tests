package travel.snapshot.dp.qa.junit.utils;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Map used for test entities. If non-existing map key is requested in a test scenario the test will fail instead of the map returning null
 * and the test failing with some nonsense exception somewhere in the dp-api-tests core code.
 */
public class EntityNonNullMap<K,V> extends HashMap<K,V> {

    public EntityNonNullMap(Map<K,V> Map) {
        super(Map);
    }

    @Override
    public V get(Object key) {
        if(!super.containsKey(key)){
            fail("Entity " + key.toString() + " not found in test data. Check your yaml files!");
        }
        return super.get(key);
    }
}
