
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mifmif.common.regex.Generex;
import java.util.HashMap;
import java.util.Map;
import travel.snapshot.dp.qa.helpers.ResponseEntry;
import static com.jayway.restassured.RestAssured.given;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import static org.hamcrest.Matchers.is;
import org.junit.Assert;
import travel.snapshot.dp.qa.helpers.FieldType;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;

/**
 *
 * @author konkol
 */
public class CommonObjectSteps extends BasicSteps {
    
    private static final String SERENITY__PREFIX_OBJECT_DEFINITION = "object_def:";
    private static final String SERENITY__PREFIX_OBJECT_LOCATION = "object_loc:";
    private static final String SERENITY__PREFIX_OBJECT_ID_FIELD = "object_id_field:";
    
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
        String json = mapper.writeValueAsString(getCorrectObject(objectName));
        setSessionResponse(restCreateObject(getObjectLocation(objectName), json));
    }
    
    @Step
    public void updateCorrectObject(String objectName) throws IOException {
        ObjectNode correctObject = getCorrectObject(objectName);
        String json = mapper.writeValueAsString(correctObject);
        Response correctObjectResponse = restCreateObject(getObjectLocation(objectName), json);
        
        String objectID = getJsonTree(correctObjectResponse).get(getObjectIDField(objectName)).asText();
        String etag = correctObjectResponse.getHeader("ETag");
        
        ObjectNode updateObject = getCorrectObject(objectName);
        updateObject.set("property_code", correctObject.get("property_code"));
        
        // generate completely new object and update the old by id and etag
        setSessionResponse(restUpdateObject(getObjectLocation(objectName),
            objectID, etag, mapper.writeValueAsString(updateObject)));
    }
    
    @Step
    public void createInvalidObjects(String objectName) throws JsonProcessingException {
        BiConsumer<ObjectNode, ObjectField> op = (node, field) -> node.set(
            getJsonProperty(field),
            getJsonNode(field, generateFieldValue(field.getInvalid())));
        sendObjects(objectName, generateObjects(objectName, op));
    }
    
    @Step
    public void createObjectsWithLongFields(String objectName) throws JsonProcessingException {
        BiConsumer<ObjectNode, ObjectField> op = (node, field) -> node.set(
            getJsonProperty(field),
            getJsonNode(field, generateFieldValue(field.getLonger())));
        sendObjects(objectName, generateObjects(objectName, op));
    }

    @Step
    public void createObjectsWithMissingFields(String objectName) throws JsonProcessingException {
        BiConsumer<ObjectNode, ObjectField> op = (node, field) -> node.remove(getJsonProperty(field));
        sendObjects(objectName, generateObjects(objectName, op));
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
    
    @Step
    public void verifyLocationObject() throws IOException {
        Response stored = getSessionResponse();
        Response current = given().spec(spec)
            .get(stored.header("location"));
        
        Assert.assertThat(getJsonTree(current), is(getJsonTree(stored)));
    }
    
    @Step
    public void updateObjectsWithInvalidValues(String objectName) throws IOException {
        BiConsumer<ObjectNode, ObjectField> op = (node, f) -> node.set(
            getJsonProperty(f),
            getJsonNode(f, generateFieldValue(f.getInvalid())));
        updateObjects(objectName, op);
    }
    
    @Step
    public void updateObjectsWithCorrectValues(String objectName) throws IOException {
        BiConsumer<ObjectNode, ObjectField> op = (node, f) -> node.set(
            getJsonProperty(f),
            getJsonNode(f, generateFieldValue(f.getCorrect())));
        updateObjects(objectName, op);
    }
    
    // --- rest ---
    
    private Response restCreateObject(String objectLocation, String json) {
        return given().spec(spec).basePath(objectLocation)
            .body(json)
            .when().post();
    }
    
    private Response restUpdateObject(String objectLocation, String id, String etag, String json) {
        return given().spec(spec).basePath(objectLocation + "/" + id)
            .header("If-Match", etag)
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
    
    public void setObjectIDField(String objectName, String fieldName) {
        Serenity.setSessionVariable(SERENITY__PREFIX_OBJECT_ID_FIELD + objectName).to(fieldName);
    }
    
    public String getObjectIDField(String objectName) {
        return Serenity.<String>sessionVariableCalled(SERENITY__PREFIX_OBJECT_ID_FIELD + objectName);
    }
    
    // --- json ---
    
    private String getJsonProperty(ObjectField field) {
        return JsonPointer.compile(field.getName()).last().getMatchingProperty();
    }
    
    private JsonNode getJsonNode(ObjectField field, String value) {
        return FieldType.getType(field.getType()).getJsonNode(value);
    }
    
    private JsonNode getJsonTree(Response response) throws IOException {
        return mapper.readTree(response.body().asString());
    }
    
    // --- other ---
    
    private ObjectNode getCorrectObject(String objectName) {
        ObjectNode root = factory.objectNode();
        getObjectDefinition(objectName).stream()
            .filter((field) -> filterField(field.getCorrect()))
            .forEach((field) -> applyNodeOperation(root, field, (n, f) -> n.set(
                    getJsonProperty(f),
                    FieldType.getType(f.getType()).getJsonNode(generateFieldValue(f.getCorrect())))));
        return root;
    }
    
    private ObjectNode applyNodeOperation(ObjectNode root, ObjectField field, BiConsumer<ObjectNode, ObjectField> op) {
        String property;
        ObjectNode current = root;
        JsonPointer it = JsonPointer.compile(field.getName());
        while(!(property = it.getMatchingProperty()).equals(it.last().getMatchingProperty())) {
            current = root.with(property);
            it = it.tail();
        }
        
        op.accept(current, field);
        
        // allow chaining
        return root;
    }
    
    private Map<String, ObjectNode> generateObjects(String objectName, BiConsumer<ObjectNode, ObjectField> op) {
        return getObjectDefinition(objectName).stream().collect(Collectors.toMap(
            (f) -> f.getName(),
            (f) -> applyNodeOperation(getCorrectObject(objectName), f, op)));
    }
    
    private void updateObjects(String objectName, BiConsumer<ObjectNode, ObjectField> op) throws IOException {
        HashMap<String, Response> responses = new HashMap<>();
        for(ObjectField field : getObjectDefinition(objectName)) {
            if (filterField(field.getInvalid())) {
                // #1 create correct object server-side
                ObjectNode correctObject = getCorrectObject(objectName);
                Response correctObjectResponse = restCreateObject(getObjectLocation(objectName),
                    mapper.writeValueAsString(correctObject));
                
                // #2 extract object ID and ETag from response
                String objectID = getJsonTree(correctObjectResponse).get(getObjectIDField(objectName)).asText();
                String etag = correctObjectResponse.getHeader("ETag");
                
                // #3 update object value for current field test
                applyNodeOperation(correctObject, field, op);
                
                // #4 update object server-side
                Response updatedObjectResponse = restUpdateObject(getObjectLocation(objectName),
                    objectID, etag, mapper.writeValueAsString(correctObject));
                
                // #5 store responses for evaluation in next steps
                responses.put(field.getName(), updatedObjectResponse);
            }
        }
        
        setSessionResponseMap(responses);
    }
    
    private void sendObjects(String objectName, Map<String, ObjectNode> invalidObjects) throws JsonProcessingException {
        HashMap<String, Response> responses = new HashMap<>();
        for(Map.Entry<String, ObjectNode> entry : invalidObjects.entrySet()) {
            logger.info("sending json for " + entry.getKey());
            String json = mapper.writeValueAsString(entry.getValue());
            Response response = restCreateObject(getObjectLocation(objectName), json);
            responses.put(entry.getKey(), response);
        }
        
        setSessionResponseMap(responses);
    }
    
    private boolean filterField(String value) {
        return new NullEmptyStringConverter().transform(value) != null;
    }
    
    private String generateFieldValue(String regex) {
        return new Generex(regex).random();
    }
    
}
