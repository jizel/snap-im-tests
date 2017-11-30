package travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface GoogleAnalyticsDwhDao {

    @Select("SELECT * FROM Fact_web_performance_cumulative f WHERE f.dim_date_id = #{dateId} AND f.dim_property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    Map<String, Object> getData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

}
