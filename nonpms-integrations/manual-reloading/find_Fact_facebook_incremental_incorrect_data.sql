SELECT 	
	count(d1),
	t.*
FROM
	(
        SELECT   
            openInterval.property_id AS p_id,
            openInterval.date_id AS d1, 
            propertyDay.date_id AS d2,
            (COALESCE(facebookDay.incremental_number_of_posts, 0) - COALESCE(propertyDay.incremental_number_of_posts, 0)) AS t_posts,
            openInterval.incremental_number_of_posts AS post_o1, facebookDay.incremental_number_of_posts AS post_c1, propertyDay.incremental_number_of_posts AS post_o2,
            (COALESCE(facebookDay.incremental_engagements, 0) - COALESCE(propertyDay.incremental_engagements, 0)) AS t_enga,
            openInterval.incremental_engagements AS enga_o1, facebookDay.incremental_engagements AS enga_c1, propertyDay.incremental_engagements AS enga_o2,
            (COALESCE(facebookDay.incremental_impressions, 0) - COALESCE(propertyDay.incremental_impressions, 0)) AS t_impre,
            openInterval.incremental_impressions AS impre_o1, facebookDay.incremental_impressions AS impre_c1, propertyDay.incremental_impressions AS impre_o2,
            (COALESCE(facebookDay.incremental_reach, 0) - COALESCE(propertyDay.incremental_reach, 0)) AS t_reach,
            openInterval.incremental_reach AS reach_o1, facebookDay.incremental_reach AS reach_c1, propertyDay.incremental_reach AS reach_o2,
            (COALESCE(facebookDay.incremental_likes, 0) - COALESCE(propertyDay.incremental_likes, 0)) AS t_likes,
            openInterval.incremental_likes AS likes_o1, facebookDay.incremental_likes AS likes_c1, propertyDay.incremental_likes AS likes_o2,
            (COALESCE(facebookDay.incremental_unlikes, 0) - COALESCE(propertyDay.incremental_unlikes, 0)) AS t_unlikes,
            openInterval.incremental_unlikes AS unlikes_o1, facebookDay.incremental_unlikes AS unlikes_c1, propertyDay.incremental_unlikes AS unlikes_o2
        FROM (
            SELECT *
            FROM dp.Fact_facebook_raw
            WHERE datapoint = 1
--                 AND property_id = #{propertyId}
--                 AND date_id = #{incParams.dateId}            
                -- Set date since the data are being reloaded            
                AND date_id >= 20170501
            ) openInterval
        LEFT JOIN (
            SELECT *
            FROM dp.Fact_facebook_raw
            WHERE datapoint = 2
--                 AND property_id = #{propertyId}
--                 AND date_id = #{incParams.dateId}
            ) facebookDay ON openInterval.property_id = facebookDay.property_id
            AND openInterval.date_id = facebookDay.date_id
        LEFT JOIN (
            SELECT *
            FROM dp.Fact_facebook_raw
            WHERE datapoint = 1
--                 AND property_id = #{propertyId}
--                 AND date_id = #{incParams.dateIdPrev}
            ) propertyDay ON openInterval.property_id = propertyDay.property_id
            AND DATE(openInterval.date_id) - INTERVAL 1 DAY = propertyDay.date_id
	) AS t
WHERE
	t_posts < 0 OR t_enga < 0 OR t_impre < 0 OR t_reach < 0 OR t_likes < 0 OR t_unlikes < 0
GROUP BY d1

