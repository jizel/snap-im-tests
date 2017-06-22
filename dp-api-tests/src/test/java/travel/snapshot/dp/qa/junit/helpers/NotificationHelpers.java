package travel.snapshot.dp.qa.junit.helpers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertTrue;

import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * This helper class represents the JMS notification.
 */

@NoArgsConstructor
public class NotificationHelpers {

    public static void verifyNotification(Map<String, Object> expectedMap, Map<String, Object> actualMap) {
        expectedMap.forEach((k,v) -> assertThat(actualMap, hasEntry(k,v)));
        assertTrue(actualMap.containsKey("timestamp"));
        assertTrue(actualMap.containsKey("entity_id"));
    }

}
