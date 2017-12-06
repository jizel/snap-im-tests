package travel.snapshot.dp.qa.nonpms.etl.test.dal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface TripAdvisorDwhDao extends IntegrationDwhDao {

    @Select("SELECT * FROM Fact_ta_daily WHERE date_id = #{dateId} AND property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    Map<String, Object> getData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

    @Delete("DELETE FROM Fact_ta_daily WHERE date_id >= #{since} AND date_id <= #{until} AND property_id = " +
            "( SELECT property_id FROM Dim_property WHERE property_key = #{propertyKey} )")
    void deleteData(@Param("propertyKey") String propertyId, @Param("since") Integer since, @Param("until") Integer until);

    @Insert("REPLACE INTO Fact_ta_daily (property_id, date_id, index_rank, index_rank_total_number, bubble_rating, number_of_reviews, rating_score_1, rating_score_2, rating_score_3, rating_score_4, rating_score_5, rate_location, rate_sleep, rate_room, rate_service, rate_value, rate_cleanliness) " +
            "SELECT property_id, #{dateId}, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 FROM Dim_property WHERE property_key = #{propertyKey}")
    void insertData(@Param("propertyKey") String propertyId, @Param("dateId") Integer dateId);

}
