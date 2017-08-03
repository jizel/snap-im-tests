package travel.snapshot.dp.qa.junit.helpers;

import net.serenitybdd.core.Serenity;

import java.util.ArrayList;
import java.util.Map;

import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.ENTITIES_TO_DELETE;

public class CommonHelpers {

    public void updateRegistryOfDeletables(String entityType, String id) {
        // Retrieve the map from serenity session variable
        Map<String, ArrayList<String>> registry = Serenity.sessionVariableCalled(ENTITIES_TO_DELETE);
        // Retrieve the array of ids of the certain enity type
        ArrayList<String> listIds = getArrayFromMap(entityType, registry);
        // Update this list
        listIds.add(id);
        // Put it back to the map and map to session variable
        registry.put(entityType, listIds);
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(registry);
    }

    public ArrayList<String> getArrayFromMap(String aKey, Map<String, ArrayList<String>> inputMap) {
        return (inputMap.get(aKey) == null) ? new ArrayList<>() : inputMap.get(aKey);
    }
}
