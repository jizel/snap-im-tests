package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class ImplicitParentChildAccess extends CommonTest {

    UUID userWithAccessId = null;
    UUID userWithOutAccessId = null;
    UUID psId = null;
    List<UUID> propertyIds = null;

    @Before
    public void setUp() {
        super.setUp();
        userWithAccessId = entityIsCreated(testUser1);
        userWithOutAccessId = entityIsCreated(testUser2);
        psId = entityIsCreated(testPropertySet1);
        propertyIds = new ArrayList<UUID>();
        IntStream.range(0, 2).forEachOrdered(n -> {
            testProperty1.setName(String.format("prop_name_%d", n));
            testProperty1.setId(null);
            testProperty1.setCode(null);
            propertyIds.add(entityIsCreated(testProperty1));
        });
    }

    @Test
    public void implicitAccessToPsByUserWithAccessToAllProperties() {
        propertyIds.forEach(propertyId -> {
            entityIsCreated(constructPropertySetPropertyRelationship(psId, propertyId, true));
            entityIsCreated(constructUserPropertyRelationshipDto(userWithAccessId, propertyId, true));
        });
        entityIsCreated(constructUserPropertyRelationshipDto(userWithOutAccessId, propertyIds.get(0), true));
        List<PropertySetDto> returnedSets1 = getEntitiesAsTypeByUserForApp(userWithAccessId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, emptyQueryParams());
        List<PropertySetDto> returnedSets2 = getEntitiesAsTypeByUserForApp(userWithOutAccessId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, emptyQueryParams());
        assertThat(returnedSets1).hasSize(1);
        assertThat(returnedSets2).hasSize(0);
    }

    @Test
    public void implicitAccessToPropertyByUserWithAccessToPs() {
        propertyIds.forEach(propertyId -> {
                    entityIsCreated(constructPropertySetPropertyRelationship(psId, propertyId, true));
        });
        List<PropertyDto> returnedProperties = getEntitiesAsTypeByUserForApp(userWithAccessId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTIES_PATH, PropertyDto.class, emptyQueryParams());
        assertThat(returnedProperties).hasSize(0);
        entityIsCreated(constructUserPropertySetRelationshipDto(userWithAccessId, psId, true));
        List<PropertyDto> returnedProperties1 = getEntitiesAsTypeByUserForApp(userWithAccessId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTIES_PATH, PropertyDto.class, emptyQueryParams());
        assertThat(returnedProperties1).hasSize(2);
    }
}
