package travel.snapshot.dp.qa.nonpms.etl.test.dal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface InstagramDwhDao extends IntegrationDwhDao {

    @Select("SELECT * FROM Fact_instagram_daily WHERE date_id = #{dateId} AND property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    Map<String, Object> getData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

    @Delete("DELETE FROM Fact_instagram_daily WHERE date_id >= #{since} AND date_id <= #{until} AND property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    void deleteData(@Param("propertyKey") String propertyId, @Param("since") Integer since, @Param("until") Integer until);

    @Insert("REPLACE INTO Fact_instagram_daily (property_id, date_id, posts_count, followers_count, likes_sum, comments_sum, hashtag_count, hashtag_value) " +
            "SELECT property_id, #{dateId}, 0, 0, 0, 0, 0, 0 FROM Dim_property WHERE property_key = #{propertyKey}")
    void insertData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

}
