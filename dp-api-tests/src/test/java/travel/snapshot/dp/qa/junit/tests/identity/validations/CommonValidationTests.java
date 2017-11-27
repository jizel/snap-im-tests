package travel.snapshot.dp.qa.junit.tests.identity.validations;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_PRECONDITION_FAILED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.qa.cucumber.helpers.FieldType.JSON;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ETAG;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtag;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.api.model.VersionedEntityDto;
import travel.snapshot.dp.qa.cucumber.helpers.ObjectField;
import travel.snapshot.dp.qa.junit.helpers.ValidationHelpers;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Validation tests - valid, invalid, longer and missing attribute values for entities.
 *
 * Concrete entity validations tests should just extend this abstract class and provide their own Getters.
 */
public abstract class CommonValidationTests extends CommonTest {

    private ValidationHelpers validationHelpers;
    protected Random random = new Random();

    private static Predicate<ObjectField> nonNullCorrectFilter = (f) -> nonNull(f.getCorrect());
    private static Predicate<ObjectField> nonNullInvalidFilter = (f) -> nonNull(f.getInvalid());
    private static Predicate<ObjectField> nonNullLongerFilter = (f) -> nonNull(f.getLonger());
    private static Predicate<ObjectField> requiredFilter = ObjectField::getRequired;
//    Hack for User's user_customer_relationship which behaves absolutely differently than all the other attributes.
//    It can be removed when user_customer_relationship is removed (hopefully soon)
    private static Predicate<ObjectField> nonJsonFilter = (f) -> f.getType() != JSON;

    private static final int FILTERING_COUNT = 30;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        validationHelpers = new ValidationHelpers();
    }

    protected abstract List<ObjectField> getAttributesBoundaries();
    protected abstract String getPath();
    protected abstract EntityDto getTestEntity();
    protected abstract <DTO> Class<DTO> getDtoType();
    protected abstract <DTOA> Class<DTOA> getDtoArrayType();

    /**
     * Default method implementation. Valid updates may differ for various entities.
     * Concrete test classes should override this.
     */
    protected ObjectNode getValidUpdate(ObjectNode objectNode) {
        return objectNode;
    }

    @Test
    public void createCorrectValues() {
        createEntity(getPath(), validationHelpers.getCorrectObject(getAttributesBoundaries()))
                .then()
                .statusCode(SC_CREATED);
    }

    @Test
    public void updateCorrectValues() {
        UUID createdEntityId = entityIsCreated(getTestEntity());
        ObjectNode updateObject = getValidUpdate(validationHelpers.getCorrectObject(getAttributesBoundaries()));
        updateEntity(getPath(), createdEntityId, updateObject)
                .then()
                .statusCode(SC_OK);
    }

    @Test
    public void updateCorrectValueOneByOne() {
        UUID createdEntityId = entityIsCreated(getTestEntity());

        getAttributesBoundaries().stream().filter(nonNullCorrectFilter).forEach(objectField -> {
            ObjectNode updateObject = (new JsonNodeFactory(false)).objectNode();
            validationHelpers.setFieldToCorrectValue(updateObject, objectField);
            updateObject = getValidUpdate(updateObject);
            updateEntity(getPath(), createdEntityId, updateObject)
                    .then()
                    .statusCode(SC_OK);
        });
    }

    @Test
    @Tag(SLOW_TEST)
    public void filteringValidation() {
        List<VersionedEntityDto> createdEntities = validationHelpers.createNObjectsAs(
                getDtoType(),
                getPath(),
                getAttributesBoundaries(),
                FILTERING_COUNT
        );
        for (VersionedEntityDto entity : createdEntities) {
            for (ObjectField field : getAttributesBoundaries()) {
                validationHelpers.filterByField(entity, field).ifPresent(response -> {
                    List<VersionedEntityDto> returnedEntities = asList(response.as(getDtoArrayType()));
                    assertThat(returnedEntities).contains(entity);
                });
            }
        }
    }

    @Test
    public void createInvalidValues() {
        getAttributesBoundaries().stream().filter(nonNullInvalidFilter).forEach(objectField -> {
            ObjectNode entity = validationHelpers.getCorrectObject(getAttributesBoundaries());
            validationHelpers.setFieldToInvalidValue(entity, objectField);
            createEntity(getPath(), entity)
                    .then().statusCode(SC_UNPROCESSABLE_ENTITY)
                    .assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        });
    }

    @Test
    public void createMissingValues() {
        getAttributesBoundaries().stream().filter(requiredFilter).filter(nonJsonFilter).forEach(objectField -> {
            ObjectNode entity = validationHelpers.getCorrectObject(getAttributesBoundaries());
            validationHelpers.removeNodeField(entity, objectField);
            createEntity(getPath(), entity)
                    .then().statusCode(SC_UNPROCESSABLE_ENTITY)
                    .assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        });
    }

    @Test
    public void createLongerValues() {
        getAttributesBoundaries().stream().filter(nonNullLongerFilter).forEach(objectField -> {
            ObjectNode entity = validationHelpers.getCorrectObject(getAttributesBoundaries());
            validationHelpers.setFieldToLongerValue(entity, objectField);
            createEntity(getPath(), entity)
                    .then().statusCode(SC_UNPROCESSABLE_ENTITY)
                    .assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        });
    }

    @Test
    public void updateInvalidOneByOne() {
        UUID createdEntityId = entityIsCreated(getTestEntity());

        getAttributesBoundaries().stream().filter(nonNullInvalidFilter).forEach(objectField -> {
            ObjectNode updateObject = (new JsonNodeFactory(false)).objectNode();
            validationHelpers.setFieldToInvalidValue(updateObject, objectField);
            updateEntity(getPath(), createdEntityId, updateObject)
                    .then()
                    .statusCode(SC_UNPROCESSABLE_ENTITY);
        });
    }

    @Test
    public void updateLongerValues() {
        UUID createdEntityId = entityIsCreated(getTestEntity());

        getAttributesBoundaries().stream().filter(nonNullLongerFilter).forEach(objectField -> {
            ObjectNode updateObject = (new JsonNodeFactory(false)).objectNode();
            validationHelpers.setFieldToLongerValue(updateObject, objectField);
            updateEntity(getPath(), createdEntityId, updateObject)
                    .then()
                    .statusCode(SC_UNPROCESSABLE_ENTITY);
        });
    }

    @Test
    public void updateInvalidMissingEtag() {
        UUID createdEntityId = entityIsCreated(getTestEntity());
        ObjectNode updateObject = getValidUpdate(validationHelpers.getCorrectObject(getAttributesBoundaries()));

        updateEntityWithEtag(getPath(), createdEntityId, updateObject, NON_EXISTENT_ETAG)
                .then()
                .statusCode(SC_PRECONDITION_FAILED)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_INVALID_ETAG));

        updateEntityWithEtag(getPath(), createdEntityId, updateObject, null)
                .then()
                .statusCode(SC_PRECONDITION_FAILED)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_MISSING_ETAG));
    }

}
