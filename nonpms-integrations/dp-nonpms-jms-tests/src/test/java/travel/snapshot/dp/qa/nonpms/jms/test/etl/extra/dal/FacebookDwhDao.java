package travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface FacebookDwhDao extends IntegrationDwhDao {

    @Select("SELECT * FROM Fact_facebook_cumulative WHERE date_id = #{dateId} AND property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    Map<String, Object> getData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);


    @Delete("DELETE FROM Fact_facebook_cumulative WHERE date_id >= #{since} AND date_id <= #{until} AND property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    void deleteData(@Param("propertyKey") String propertyId, @Param("since") Integer since, @Param("until") Integer until);

    @Insert("REPLACE INTO Fact_facebook_cumulative (property_id, date_id, followers, number_of_posts, engagements, impressions, reach, likes, unlikes) " +
            "SELECT property_id, #{dateId}, 0, 0, 0, 0, 0, 0, 0 FROM Dim_property WHERE property_key = #{propertyKey}")
    void insertData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

}
