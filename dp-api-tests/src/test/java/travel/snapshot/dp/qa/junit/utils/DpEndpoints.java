package travel.snapshot.dp.qa.junit.utils;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.EFFECTIVE_PERMISSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_COMMERCIAL_SUBSCRIPTION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DpEndpoints {

    public static final List<String> READ_WRITE_ENDPOINTS = new ArrayList<>(endpointDtoMap.keySet());

    public static final List<String> ALL_ENDPOINTS;

    static {
        ALL_ENDPOINTS =  new ArrayList<>();
        ALL_ENDPOINTS.addAll(READ_WRITE_ENDPOINTS);
        ALL_ENDPOINTS.add(EFFECTIVE_PERMISSIONS_PATH);
    }

    public static final Map<String, UUID> ENDPOINTS_WITH_IDS_MAP;

    static {
        ENDPOINTS_WITH_IDS_MAP = new HashMap<>();
        ENDPOINTS_WITH_IDS_MAP.put(APPLICATIONS_PATH, DEFAULT_SNAPSHOT_APPLICATION_ID);
        ENDPOINTS_WITH_IDS_MAP.put(APPLICATION_VERSIONS_PATH, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        ENDPOINTS_WITH_IDS_MAP.put(PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        ENDPOINTS_WITH_IDS_MAP.put(CUSTOMERS_PATH, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        ENDPOINTS_WITH_IDS_MAP.put(USERS_PATH, DEFAULT_SNAPSHOT_USER_ID);
        ENDPOINTS_WITH_IDS_MAP.put(PARTNERS_PATH, DEFAULT_SNAPSHOT_PARTNER_ID);
        ENDPOINTS_WITH_IDS_MAP.put(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
    }

    public static final List<String> ENDPOINTS_WITH_IDS;

    static {
        ENDPOINTS_WITH_IDS = new ArrayList<>();
        ENDPOINTS_WITH_IDS_MAP.forEach((k,v) -> {
            ENDPOINTS_WITH_IDS.add(String.format("%s/%s", k, v));
        });
    }

}
