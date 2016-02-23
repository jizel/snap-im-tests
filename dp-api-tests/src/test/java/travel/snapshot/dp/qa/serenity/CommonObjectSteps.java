package travel.snapshot.dp.qa.serenity;

import com.google.common.collect.Lists;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.response.Response;
import com.mifmif.common.regex.Generex;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import travel.snapshot.dp.qa.helpers.FieldType;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.helpers.ObjectField;
import travel.snapshot.dp.qa.helpers.ObjectMappers;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.ResponseEntry;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static travel.snapshot.dp.qa.helpers.ObjectMappers.OBJECT_MAPPER;

/**
 * @author konkol
 */
public class CommonObjectSteps extends BasicSteps {

    private static final String SERENITY__PREFIX_OBJECT_DEFINITION = "object_def:";
    private static final String SERENITY__PREFIX_OBJECT_LOCATION = "object_loc:";
    private static final String SERENITY__PREFIX_OBJECT_ID_FIELD = "object_id_field:";
    private static final String SERENITY__PREFIX_OBJECT_SENT = "object_sent:";
    private static final String SERENITY__PREFIX_OBJECT_RECEIVED = "object_received:";

    private final Logger logger = LoggerFactory.getLogger(CommonObjectSteps.class);
    private final JsonNodeFactory factory;

    public CommonObjectSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        factory = new JsonNodeFactory(false);

    }

    @Step
    public void createCorrectObject(String objectName) throws IOException {
        ObjectNode jsonObject = getCorrectObject(objectName);
        String jsonString = getJsonString(jsonObject);
        Response response = restCreateObject(getObjectLocation(objectName), jsonString);

        // store original and returned objects for comparison
        setSessionVariable(SERENITY__PREFIX_OBJECT_SENT + objectName, jsonObject);
        setSessionVariable(SERENITY__PREFIX_OBJECT_RECEIVED + objectName, getJsonNode(response));

        // store response for comparison
        setSessionResponse(response);
    }

    @Step
    public void updateCorrectObject(String objectName) throws IOException {
        ObjectNode correctObject = getCorrectObject(objectName);
        String jsonString = getJsonString(correctObject);
        Response correctObjectResponse = restCreateObject(getObjectLocation(objectName), jsonString);

        String objectID = getJsonNode(correctObjectResponse).get(getObjectIDField(objectName)).asText();
        String etag = correctObjectResponse.getHeader(HEADER_ETAG);

        // create a completely new object for update
        ObjectNode updateObject = getCorrectObject(objectName);

        // update old record by id
        // store response for HTTP status comparison
        setSessionResponse(restUpdateObject(getObjectLocation(objectName),
                objectID, etag, getJsonString(updateObject)));

        // store update object for comparison
        // store original and returned objects for comparison
        setSessionVariable(SERENITY__PREFIX_OBJECT_SENT + objectName, updateObject);
        setSessionVariable(SERENITY__PREFIX_OBJECT_RECEIVED + objectName,
                getJsonNode(restGetObject(getObjectLocation(objectName), objectID)));
    }

    @Step
    public void createInvalidObjects(String objectName) throws JsonProcessingException {
        BiConsumer<ObjectNode, ObjectField> op = (node, field) -> node.set(
                getJsonProperty(field),
                getJsonNode(field, generateFieldValue(field.getInvalid())));
        Predicate<ObjectField> filter = (field) -> !isNullField(field.getInvalid());
        sendObjects(objectName, generateObjects(objectName, op, filter));
    }

    @Step
    public void createObjectsWithLongFields(String objectName) throws JsonProcessingException {
        BiConsumer<ObjectNode, ObjectField> op = (node, field) -> node.set(
                getJsonProperty(field),
                getJsonNode(field, generateFieldValue(field.getLonger())));
        Predicate<ObjectField> filter = (field) -> !isNullField(field.getLonger());
        sendObjects(objectName, generateObjects(objectName, op, filter));
    }

    @Step
    public void createObjectsWithMissingFields(String objectName) throws JsonProcessingException {
        BiConsumer<ObjectNode, ObjectField> op = (node, field) -> node.remove(getJsonProperty(field));
        sendObjects(objectName, generateObjects(objectName, op, (f) -> true));
    }

    @Step
    public void responsesMatch(List<ResponseEntry> entries) {
        Map<String, Response> responses = getSessionResponseMap();
        for (ResponseEntry entry : entries) {
            Response response = responses.get(entry.getTestedField());
            response.then().statusCode(entry.getResponseCode());

            // only test custom code requested
            if (entry.getCustomCode() != null) {
                response.then().body("code", is(entry.getCustomCode()));
            }
        }
    }

    @Step
    public void verifyLocationObject() throws IOException {
        Response stored = getSessionResponse();
        Response current = given().spec(spec).get(stored.header("location"));
        Assert.assertThat(getJsonNode(current), is(getJsonNode(stored)));
    }

    @Step
    public void updateObjectsWithInvalidValues(String objectName) throws IOException {
        BiConsumer<ObjectNode, ObjectField> op = (node, f) -> node.set(
                getJsonProperty(f),
                getJsonNode(f, generateFieldValue(f.getInvalid())));
        Predicate<ObjectField> filter = (f) -> !isNullField(f.getInvalid());
        updateObjects(objectName, op, filter);
    }

    @Step
    public void updateObjectsWithCorrectValues(String objectName) throws IOException {
        BiConsumer<ObjectNode, ObjectField> op = (node, f) -> node.set(
                getJsonProperty(f),
                getJsonNode(f, generateFieldValue(f.getCorrect())));
        Predicate<ObjectField> filter = (f) -> !isNullField(f.getCorrect());
        updateObjects(objectName, op, filter);
    }

    @Step
    public void createObjectsForSession(int count, String objectName) throws IOException {
        HashMap<String, Response> objects = new HashMap<>();
        for (int index = 0; index < count; index++) {
            Response response = restCreateObject(getObjectLocation(objectName), getJsonString(getCorrectObject(objectName)));
            String objectID = getJsonNode(response).get(getObjectIDField(objectName)).asText();
            objects.put(objectID, response);
        }

        // store created objects as id-response map
        setSessionResponseMap(objects);
    }

    @Step
    public void filterObjectsByFields(String objectName) throws IOException {
        // get previously created objects
        Map<String, JsonNode> objects = getObjectsById(getSessionResponseMap());

        List<ObjectField> definition = getObjectDefinition(objectName);
        for (ObjectField field : definition) {
            // filter only top-level fields, ignore inner objects
            if (field.isTopLevel()) {
                for (Map.Entry<String, JsonNode> entry : objects.entrySet()) {
                    // get filtering value from current object
                    JsonNode object = entry.getValue();
                    JsonPointer pointer = JsonPointer.compile(field.getPath());
                    String filterValue = object.at(pointer).asText();

                    // do the filtering
                    JsonNode result =
                            getJsonNode(restFilterObject(getObjectLocation(objectName),
                                    field.getName() + "==" + FieldType.getType(field.getType()).getFilterValue(filterValue)));

                    // search results should always be packed in arrays
                    if (result.isArray()) {
                        List<JsonNode> returnedObjects = Lists.newArrayList(result.iterator());

                        // check that our object was found
                        Assert.assertThat(returnedObjects, hasItem(object));

                        // check that all returned objects have correct field value
                        Assert.assertThat(
                                returnedObjects.stream().map((node) -> node.at(pointer).asText()).collect(Collectors.toList()),
                                everyItem(is(filterValue)));
                    } else {
                        Assert.fail("Search result is not an array.");
                    }
                }
            }
        }

        // clean-up
        objects.forEach((id, json) -> restDeleteObject(getObjectLocation(objectName), id));
    }

    @Step
    public void originalAndReturnedObjectsMatch(String objectName) throws IOException {
        // get original and returned objects from session
        JsonNode originalObject = getSessionVariable(SERENITY__PREFIX_OBJECT_SENT + objectName);
        JsonNode returnedObject = getSessionVariable(SERENITY__PREFIX_OBJECT_RECEIVED + objectName);

        // remove the ID field for comparison - original object does not need to have ID included
        ((ObjectNode) returnedObject).remove(getObjectIDField(objectName));

        // is_active is special field (cannot be updated by update method, just by special api call), should not be part of generic objects
        ((ObjectNode) returnedObject).remove("is_active");

        Assert.assertThat(originalObject, is(returnedObject));
    }

    // --- rest ---

    /**
     * Create server-side object from JSON string.
     *
     * @param objectLocation object URL relative to the API root
     * @param json           a {@code String} JSON representation of the object
     * @return server response
     */
    private Response restCreateObject(String objectLocation, String json) {
        return given().spec(spec).basePath(objectLocation)
                .body(json)
                .when().post();
    }

    /**
     * Get server-side object by URL-encoded object ID.
     *
     * @param objectLocation object URL relative to the API root
     * @param id             a UUID {@code String} identifier of the object
     * @return server response
     */
    private Response restGetObject(String objectLocation, String id) {
        return given().spec(spec).basePath(objectLocation + "/" + id)
                .when().get();
    }

    /**
     * Delete server-side object by URL-encoded object ID.
     *
     * @param objectLocation object URL relative to the API root
     * @param id             a UUID {@code String} identifier of the object
     * @return server response
     */
    private Response restDeleteObject(String objectLocation, String id) {
        return given().spec(spec).basePath(objectLocation + "/" + id)
                .when().delete();
    }

    /**
     * Update server-side object by URL-encoded object ID.
     *
     * @param objectLocation object URL relative to the API root
     * @param id             a UUID {@code String} identifier of the object
     * @param etag           an ETag value for "If-Match" synch check
     * @param json           a {@code String} JSON representation of the object
     * @return server response
     */
    private Response restUpdateObject(String objectLocation, String id, String etag, String json) {
        return given().spec(spec).basePath(objectLocation + "/" + id)
                .header(HEADER_IF_MATCH, etag)
                .body(json)
                .when().post();
    }

    /**
     * Get server-side objects by URL-encoded filtering query.
     *
     * @param objectLocation object URL relative to the API root
     * @param query          a RSQL URL-encoded query
     * @return server response
     */
    private Response restFilterObject(String objectLocation, String query) {
        return given().spec(spec).basePath(objectLocation).param("limit", LIMIT_TO_ALL).param("filter", query)
                .when().get();
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

    /**
     * Object field paths contain a JSON Pointer for the position of the field in the object tree.
     * Returns the last element from the path - a string key under which the current field is stored
     * in it's parent.
     *
     * @param field source
     * @return JSON field key
     */
    private String getJsonProperty(ObjectField field) {
        return JsonPointer.compile(field.getPath()).last().getMatchingProperty();
    }

    /**
     * Creates a JsonNode subtype based on the provided {@code field}'s type.
     *
     * @param field a field definition
     * @param value a non-parsed field value
     * @return JsonNode with matching data type
     */
    private JsonNode getJsonNode(ObjectField field, String value) {
        return FieldType.getType(field.getType()).getJsonNode(value);
    }

    /**
     * Reads a response a constructs a JsonNode tree from it's body.
     *
     * @param response response with JSON content in body
     * @throws IOException for mapper failures (invalid JSON in response body, etc.)
     */
    private JsonNode getJsonNode(Response response) throws IOException {
        return OBJECT_MAPPER.readTree(response.body().asString());
    }

    /**
     * Returns textual JSON representation of the object.
     *
     * @param root root of the object tree
     * @return A JSON string
     * @throws JsonProcessingException for invalid structures
     */
    private String getJsonString(JsonNode root) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(root);
    }

    // --- other ---

    /**
     * Returns a JSON object tree filled with generated field values based on the "correct" regex,
     * defined in the object definition.
     *
     * @param objectName name of the object to create (e.g. "property", "customer", ..)
     * @return JSON object tree
     */
    private ObjectNode getCorrectObject(String objectName) {
        ObjectNode root = factory.objectNode();
        getObjectDefinition(objectName).stream()
                .filter((field) -> !isNullField(field.getCorrect()))
                .forEach((field) -> applyNodeOperation(root, field, (n, f) -> n.set(
                        getJsonProperty(f),
                        getJsonNode(f, generateFieldValue(f.getCorrect())))));
        return root;
    }

    /**
     * Given the object root and the path parameter of provided {@code field}, this method adds all
     * necessary object nodes (if such nodes are missing) on the path to the target field, relative
     * to the root object. <p/> Once the last object node - parent of the provided {@code field} -
     * is selected, the provided {@code op} is applied to it. This could for example set the value
     * of the field to the parent, remove the field from the parent and so on.
     *
     * @param root  root node for the operation
     * @param field field, relative to the root, which should be modified
     * @param op    operation to be performed on the nearest parent of the field
     * @return the root, for chaining purposes
     */
    private ObjectNode applyNodeOperation(ObjectNode root, ObjectField field, BiConsumer<ObjectNode, ObjectField> op) {
        String property;
        ObjectNode current = root;
        JsonPointer it = JsonPointer.compile(field.getPath());
        while (!(property = it.getMatchingProperty()).equals(it.last().getMatchingProperty())) {
            current = root.with(property);
            it = it.tail();
        }

        op.accept(current, field);

        // allow chaining
        return root;
    }

    /**
     * Generates a list of objects, an object for each field in the object definition. Provided
     * {@code op} is applied to a different field on each object. <p/> This way you can, for
     * example, generate objects and remove a single field from each. All resulting objects would
     * have a different field removed. Or you could create a set of objects, each with exactly one
     * invalid field, covering all combinations. <p/> Object fields to be added, set, removed, etc.
     * can be fileted with {@code filter} predicate.
     *
     * @param objectName name of the objects to be generated
     * @param op         node operation
     * @param filter     predicate to filter fields
     * @return A map of objects - key is the JSON pointer of the field on which the {@code op} was
     * applied, value is the object itself
     */
    private Map<String, ObjectNode> generateObjects(String objectName, BiConsumer<ObjectNode, ObjectField> op, Predicate<ObjectField> filter) {
        return getObjectDefinition(objectName).stream().filter(filter).collect(Collectors.toMap(
                (f) -> f.getPath(),
                (f) -> applyNodeOperation(getCorrectObject(objectName), f, op)));
    }

    /**
     * Creates and the updates objects server-side. One object is created for each field. Then that
     * field is modified on the created object by {@code op} operation. A map of responses for each
     * modification is stored into session - key is JSON pointer for modified field, value is server
     * response.
     *
     * @param objectName name of the object definition
     * @param op         operation to be performed on each field
     * @param filter     predicate to enable field filtering
     * @throws IOException for parsing problems
     */
    private void updateObjects(String objectName, BiConsumer<ObjectNode, ObjectField> op, Predicate<ObjectField> filter) throws IOException {
        HashMap<String, Response> responses = new HashMap<>();
        for (ObjectField field : getObjectDefinition(objectName)) {
            if (filter.test(field)) {
                // #1 create correct object server-side
                ObjectNode correctObject = getCorrectObject(objectName);
                Response correctObjectResponse = restCreateObject(getObjectLocation(objectName),
                        OBJECT_MAPPER.writeValueAsString(correctObject));

                // #2 extract object ID and ETag from response
                String objectID = getJsonNode(correctObjectResponse).get(getObjectIDField(objectName)).asText();
                String etag = correctObjectResponse.getHeader(HEADER_ETAG);

                // #3 update object value for current field test
                applyNodeOperation(correctObject, field, op);

                // #4 update object server-side
                Response updatedObjectResponse = restUpdateObject(getObjectLocation(objectName),
                        objectID, etag, OBJECT_MAPPER.writeValueAsString(correctObject));

                // #5 store responses for evaluation in next steps
                responses.put(field.getPath(), updatedObjectResponse);
            }
        }

        setSessionResponseMap(responses);
    }

    /**
     * Creates provided objects server-side.
     *
     * @param objectName object definition
     * @param objects    objects to be created - key is JSON pointer of a field unique for this
     *                   object, value is object itself
     */
    private void sendObjects(String objectName, Map<String, ObjectNode> objects) throws JsonProcessingException {
        HashMap<String, Response> responses = new HashMap<>();
        for (Map.Entry<String, ObjectNode> entry : objects.entrySet()) {
            responses.put(entry.getKey(), restCreateObject(getObjectLocation(objectName), getJsonString(entry.getValue())));
        }

        setSessionResponseMap(responses);
    }

    /**
     * Transaforms responses into JSON objects. Key is the object ID.
     *
     * @param responses responses
     * @return JSON objects
     * @throws IOException for parsing errors
     */
    private Map<String, JsonNode> getObjectsById(Map<String, Response> responses) throws IOException {
        HashMap<String, JsonNode> objects = new HashMap<>();
        for (Map.Entry<String, Response> entry : responses.entrySet()) {
            objects.put(entry.getKey(), getJsonNode(entry.getValue()));
        }

        return objects;
    }

    /**
     * Check if a field is in fact {@code null} as defined in the feature file - uses "/null"
     * placeholder.
     *
     * @param value value
     * @return filtered result
     */
    private boolean isNullField(String value) {
        return new NullEmptyStringConverter().transform(value) == null;
    }

    /**
     * Uses Generex library to generate a field value from provided regular expression.
     *
     * @param regex regular expression
     * @return generated value
     */
    private String generateFieldValue(String regex) {
        return new Generex(regex).random();
    }

}
