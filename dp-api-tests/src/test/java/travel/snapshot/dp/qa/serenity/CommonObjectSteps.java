
package travel.snapshot.dp.qa.serenity;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.response.Response;
import java.util.List;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.ObjectField;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mifmif.common.regex.Generex;
import java.util.HashMap;
import java.util.Map;
import travel.snapshot.dp.qa.helpers.ResponseEntry;
import static com.jayway.restassured.RestAssured.given;
import java.util.function.BiConsumer;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author konkol
 */
public class CommonObjectSteps extends BasicSteps {
    
    private static final String SERENITY__PREFIX_OBJECT_DEFINITION = "object_def:";
    private static final String SERENITY__PREFIX_OBJECT_LOCATION = "object_loc:";
    
    private final Logger logger = LoggerFactory.getLogger(CommonObjectSteps.class);
    private final ObjectMapper mapper;
    private final JsonNodeFactory factory;
    
    public CommonObjectSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        factory = new JsonNodeFactory(false);
        
        // mapper config
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }
    
    @Step
    public void createCorrectObject(String objectName) throws JsonProcessingException {
        ObjectNode root = factory.objectNode();
        fillJsonObject(root, getObjectDefinition(objectName),
            (ObjectField source) -> generateFieldValue(source.getCorrect()));
        String json = mapper.writeValueAsString(root);
        setSessionResponse(createObject(objectName, json));
    }
    
    @Step
    public void createInvalidObjects(String objectName) throws JsonProcessingException {
        // fields values should be generated from invalid patterns
        Function<ObjectField, String> valueProvider =
            (ObjectField source) -> generateFieldValue(source.getInvalid());
        
        // correct value should be replaced by invalid value
        BiConsumer<ObjectNode, ObjectField> nodeOperation =
            (ObjectNode parent, ObjectField field) -> parent.set(field.getName(), field.asJsonNode(valueProvider));
        
        generateObjectsAndSend(objectName, valueProvider, nodeOperation);
    }
    
    @Step
    public void createObjectsWithLongFields(String objectName) throws JsonProcessingException {
        // fields values should be generated from invalid patterns
        Function<ObjectField, String> valueProvider =
            (ObjectField source) -> generateFieldValue(source.getLonger());
        
        // correct value should be replaced by invalid value
        BiConsumer<ObjectNode, ObjectField> nodeOperation =
            (ObjectNode parent, ObjectField field) -> parent.set(field.getName(), field.asJsonNode(valueProvider));
        
        generateObjectsAndSend(objectName, valueProvider, nodeOperation);
    }

    @Step
    public void createObjectsWithMissingFields(String objectName) throws JsonProcessingException {
        // only provide values for required fields
        Function<ObjectField, String> valueProvider =
            (ObjectField field) -> field.isRequired() ? generateFieldValue(field.getCorrect()) : null;
        
        // missing fields should be removed from the objects
        BiConsumer<ObjectNode, ObjectField> nodeOperation =
            (ObjectNode parent, ObjectField field) -> parent.remove(field.getName());
        
        generateObjectsAndSend(objectName, valueProvider, nodeOperation);
    }
    
    @Step
    public void responsesMatch(List<ResponseEntry> entries) {
        Map<String, Response> responses = getSessionResponseMap();
        for (ResponseEntry entry : entries) {
            Response response = responses.get(entry.getTestedField());
            response.then().statusCode(entry.getResponseCode());
            response.then().body("code", is(entry.getCustomCode()));
        }
    }
    
    // --- rest ---
    
    private Response createObject(String objectName, String json) {
        return given().spec(spec).basePath(getObjectLocation(objectName))
            .body(json)
            .when().post();
    }
    
    // --- session access ---
    
    public void setObjectDefinition(String objectName, List<ObjectField> fields) {
        Serenity.setSessionVariable(SERENITY__PREFIX_OBJECT_DEFINITION + objectName).to(fields);
    }
    
    public List<ObjectField> getObjectDefinition(String objectName) {
        return Serenity.<List<ObjectField>>sessionVariableCalled(SERENITY__PREFIX_OBJECT_DEFINITION + objectName);
    }
    
    public void setObjectLocation(String objectName, String location) {
        Serenity.setSessionVariable(SERENITY__PREFIX_OBJECT_LOCATION + objectName).to(location);
    }
    
    public String getObjectLocation(String objectName) {
        return Serenity.<String>sessionVariableCalled(SERENITY__PREFIX_OBJECT_LOCATION + objectName);
    }
    
    // --- other ---
    
    private void fillJsonObject(ObjectNode parent, List<ObjectField> fields, Function<ObjectField, String> provider) {
        for (ObjectField field : fields) {
            JsonNode node = field.asJsonNode(provider);
            parent.set(field.getName(), node);
            
            // fill inner object
            if (node instanceof ObjectNode) {
                fillJsonObject((ObjectNode) node, getObjectDefinition(provider.apply(field)), provider);
            }
        }
    }
    
    private void getUniqueObjectForEachField(
            String rootObject,
            List<ObjectField> fields,
            Map<String, ObjectNode> objects,
            String prefix,
            Function<ObjectField, String> valueProvider,
            BiConsumer<ObjectNode, ObjectField> operation) {
        for (ObjectField field : fields) {
            // process only fields with some value
            JsonNode node = field.asJsonNode(valueProvider);
            if (node != null) {
                // create a unique copy
                ObjectNode root = factory.objectNode();
                fillJsonObject(root, getObjectDefinition(rootObject),
                    (ObjectField valueSource) -> generateFieldValue(valueSource.getCorrect()));
                String pointer = prefix + "/" + field.getName();
                objects.put(pointer, root);

                // each object has a single field changed compared to the original object
                ObjectNode fieldParent = (ObjectNode) root.at(JsonPointer.compile(prefix));
                operation.accept(fieldParent, field);

                // recursion into inner objects
                if (node instanceof ObjectNode) {
                    getUniqueObjectForEachField(rootObject,
                        getObjectDefinition(valueProvider.apply(field)), objects, pointer, valueProvider, operation);
                }
            }
        }
    }
    
    private void generateObjectsAndSend(
            String objectName,
            Function<ObjectField, String> valueProvider,
            BiConsumer<ObjectNode, ObjectField> operation) throws JsonProcessingException {
        HashMap<String, ObjectNode> invalidObjects = new HashMap<>();
        getUniqueObjectForEachField(objectName, getObjectDefinition(objectName), invalidObjects,
                "", valueProvider, operation);
        
        HashMap<String, Response> responses = new HashMap<>();
        for(Map.Entry<String, ObjectNode> entry : invalidObjects.entrySet()) {
            logger.info("sending json for " + entry.getKey());
            String json = mapper.writeValueAsString(entry.getValue());
            Response response = createObject(objectName, json);
            responses.put(entry.getKey(), response);
        }
        
        setSessionResponseMap(responses);
    }
    
    private String generateFieldValue(String regex) {
        return new Generex(regex).random();
    }
    
}
