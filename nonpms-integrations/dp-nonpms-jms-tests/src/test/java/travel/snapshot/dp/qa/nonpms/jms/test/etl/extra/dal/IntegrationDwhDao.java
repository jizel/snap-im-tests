package travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal;

import java.util.Map;

public interface IntegrationDwhDao {

    Map<String, Object> getData(String propertyId, Integer dateId);

    void deleteData(String propertyId, Integer since, Integer until);

    void insertData(String propertyId, Integer dateId);

}
