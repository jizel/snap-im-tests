package travel.snapshot.dp.qa.nonpms.etl.test.dal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface GoogleAnalyticsDwhDao extends IntegrationDwhDao {

    @Select("SELECT * FROM Fact_web_performance_cumulative f WHERE f.dim_date_id = #{dateId} AND f.dim_property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    Map<String, Object> getData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

    @Delete("DELETE FROM Fact_web_performance_cumulative WHERE dim_date_id >= #{since} AND dim_date_id <= #{until} AND dim_property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    void deleteData(@Param("propertyKey") String propertyId, @Param("since") Integer since, @Param("until") Integer until);

    @Insert("REPLACE INTO Fact_web_performance_cumulative (dim_property_id, dim_date_id, users, visits, transactions, revenue) " +
            "SELECT property_id, #{dateId}, 0, 0, 0, 0 FROM Dim_property WHERE property_key = #{propertyKey}")
    void insertData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);
}
