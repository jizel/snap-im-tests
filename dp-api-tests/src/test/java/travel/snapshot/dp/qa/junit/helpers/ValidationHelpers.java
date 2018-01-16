package travel.snapshot.dp.qa.junit.helpers;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.hasItem;
import static travel.snapshot.dp.json.ObjectMappers.OBJECT_MAPPER;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.BOOL;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.ENUM;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.INTEGER;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.JSON;
import static travel.snapshot.dp.qa.junit.helpers.FieldType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.utils.EntityEndpointMapping.entityDtoEndpointMap;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.restassured.response.Response;
import com.mifmif.common.regex.Generex;
import travel.snapshot.dp.api.model.VersionedEntityDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Helpers for validation tests
 */
public class ValidationHelpers {

    private BiFunction<ObjectNode, ObjectField, ObjectNode> generateCorrectField = (node, field) -> {
        node.set(
                getJsonProperty(field),
                getJsonNode(field, generateFieldValue(field.getType(), field.getCorrect())));
        return node;
    };

    private BiFunction<ObjectNode, ObjectField, ObjectNode> generateInvalidField = (node, field) -> {
        node.set(
                getJsonProperty(field),
                getJsonNode(field, generateFieldValue(field.getType(), field.getInvalid())));
        return node;
    };

    private BiFunction<ObjectNode, ObjectField, ObjectNode> generateLongerField = (node, field) -> {
        node.set(
                getJsonProperty(field),
                getJsonNode(field, generateFieldValue(field.getType(), field.getLonger())));
        return node;
    };

    private BiFunction<ObjectNode, ObjectField, ObjectNode> removeNodeField = (node, field) -> {
        node.remove(getJsonProperty(field));
        return node;
    };

    public ObjectNode getCorrectObject(List<ObjectField> entityAttributes) {
        ObjectNode rootNode = (new JsonNodeFactory(false)).objectNode();
        entityAttributes.stream()
                .filter((field) -> (field.getCorrect() != null))
                .forEach((field) -> applyNodeOperation(rootNode, field, generateCorrectField));
        return rootNode;
    }

    public void setFieldToCorrectValue(ObjectNode objectNode, ObjectField objectField) {
        applyNodeOperation(objectNode, objectField, generateCorrectField);
    }

    public void setFieldToInvalidValue(ObjectNode objectNode, ObjectField objectField) {
        applyNodeOperation(objectNode, objectField, generateInvalidField);
    }

    public void setFieldToLongerValue(ObjectNode objectNode, ObjectField objectField) {
        applyNodeOperation(objectNode, objectField, generateLongerField);
    }

    public void removeNodeField(ObjectNode objectNode, ObjectField objectField) {
        applyNodeOperation(objectNode, objectField, removeNodeField);
    }

    /**
     * Creates #count of entities of a defined type. Be careful - if count > 50 than pagination comes into the game!
     */
    public <DTO> List<DTO> createNObjectsAs(Class<DTO> entityType, String path, List<ObjectField> objectDefinition, int count) {
        return range(0, count).mapToObj(i -> objectIsCreatedAs(entityType, path, objectDefinition)).collect(toList());
    }

    public Optional<Response> filterByField(VersionedEntityDto entity, ObjectField field) {
        // filter only top-level fields, ignore inner objects
        // skip processing any relationship fields since the system does not expose them for filtering
        if (field.isTopLevel() && !field.getType().equals(JSON)) {
            try {
                JsonNode customer = OBJECT_MAPPER.readTree(OBJECT_MAPPER.writeValueAsString(entity));
                String value = customer.get(field.getName()).asText();
                // Enums are serialized in upperCase by OBJECT_MAPPER but returned in lowercase from IM api
                value = (field.getType().equals(ENUM)) ? value.toLowerCase() : value;

                Map<String, String> params = buildQueryParamMapForPaging(null, null, field.getName() + "==" + value, null, null, null);
                Response response = getEntities(entityDtoEndpointMap.get(entity.getClass()), params)
                        .then().statusCode(SC_OK)
                        .extract().response();
                if (field.getType().equals(BOOL)) {
                    response.then().body(field.getName(), hasItem(Boolean.valueOf(value)));
                } else if (field.getType().equals(INTEGER)) {
                    response.then().body(field.getName(), hasItem(Integer.valueOf(value)));
                } else {
                    response.then().body(field.getName(), hasItem(value));
                }

                return Optional.of(response);
            } catch (IOException e) {
                throw new RuntimeException("Exception when serializing entity: " + entity.toString(), e);
            }
        }

        return Optional.empty();
    }


    private <DTO> DTO objectIsCreatedAs(Class<DTO> entityType, String path, List<ObjectField> objectDefinition) {
        return createEntity(path, getCorrectObject(objectDefinition))
                .then()
                .statusCode(SC_CREATED)
                .extract().response().as(entityType);
    }

    private ObjectNode applyNodeOperation(ObjectNode root, ObjectField field, BiFunction<ObjectNode, ObjectField, ObjectNode> op) {
        String property;
        ObjectNode current = root;
        JsonPointer it = JsonPointer.compile(field.getPath());
        while (!(property = it.getMatchingProperty()).equals(it.last().getMatchingProperty())) {
            current = root.with(property);
            it = it.tail();
        }
        return op.apply(current, field);
    }

    private String getJsonProperty(ObjectField field) {
        return JsonPointer.compile(field.getPath()).last().getMatchingProperty();
    }

    private JsonNode getJsonNode(ObjectField field, String value) {
        return field.getType().getJsonNode(value);
    }

    private String generateFieldValue(String regex) {
        return new Generex(regex).random();
    }

    private String generateFieldValue(FieldType type, String regex) {
        return type.equals(STRING) ? new Generex(regex).random() : regex;
    }
}
