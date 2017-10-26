-- Create incremental data backup table
DROP TABLE dp.Fact_facebook_incremental_bak;
CREATE TABLE dp.Fact_facebook_incremental_bak LIKE dp.Fact_facebook_incremental;
INSERT dp.Fact_facebook_incremental_bak SELECT * FROM dp.Fact_facebook_incremental;


-- Reload incremental data
REPLACE INTO dp.Fact_facebook_incremental_bak (
            property_id,
            date_id,
            total_followers,
            number_of_posts,
            engagements,
            impressions,
            reach,
            likes,
            unlikes,
            time_stamp
            )
SELECT
	property_id,
	date_id,
	coalesce(total_followers, NULL) AS total_followers,
	if(number_of_posts < 0, NULL, number_of_posts) as number_of_posts,
	if(engagements < 0, NULL, engagements) AS engagements,
	if(impressions < 0, NULL, impressions) AS impressions,
	if(reach < 0, NULL, reach) AS reach,
	if(likes < 0, NULL, likes) AS likes,
	if(unlikes < 0, NULL, unlikes) AS unlikes,
	current_timestamp AS time_stamp
FROM (
        SELECT        	
            openInterval.property_id,
            openInterval.date_id,
            openInterval.total_followers,
            -- computed data: [value at end of open interval]
            --  + [value at end of facebook day, if present]
            --  - [value at end of previous property day, if present]
            (
                openInterval.incremental_number_of_posts + coalesce(facebookDay.incremental_number_of_posts, 0) - coalesce(propertyDay.incremental_number_of_posts, 0)
                ) AS number_of_posts,
            (
                openInterval.incremental_engagements + coalesce(facebookDay.incremental_engagements, 0) - coalesce(propertyDay.incremental_engagements, 0)
                ) AS engagements,
            (
                openInterval.incremental_impressions + coalesce(facebookDay.incremental_impressions, 0) - coalesce(propertyDay.incremental_impressions, 0)
                ) AS impressions,
            (
                openInterval.incremental_reach + coalesce(facebookDay.incremental_reach, 0) - coalesce(propertyDay.incremental_reach, 0)
                ) AS reach,
            (
                openInterval.incremental_likes + coalesce(facebookDay.incremental_likes, 0) - coalesce(propertyDay.incremental_likes, 0)
                ) AS likes,
            (
                openInterval.incremental_unlikes + coalesce(facebookDay.incremental_unlikes, 0) - coalesce(propertyDay.incremental_unlikes, 0)
                ) AS unlikes,
            current_timestamp
        FROM (
            SELECT *
            FROM dp.Fact_facebook_raw
            WHERE datapoint = 1            
--                 AND property_id = #{propertyId}
--                 AND date_id = #{incParams.dateId}            
                -- Set date since the data are being reloaded                           
                AND date_id >= 20160101                
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
) AS ifps
;
