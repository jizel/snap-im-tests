REPLACE INTO dp.IncrementalFacebookPageStatistics (
            property_id,
            date_id,
            date,
            total_followers,
            incremental_likes,
            incremental_number_of_posts,
            incremental_engagements,
            incremental_impressions,
            incremental_reached,
            incremental_unlikes,
            time_stamp
            )
SELECT
	property_id,
	date_id,
	date,
	total_followers,
	if(incremental_likes < 0, 0, incremental_likes) as incremental_likes,
	if(incremental_number_of_posts < 0, 0, incremental_number_of_posts) AS incremental_number_of_posts,
	if(incremental_engagements < 0, 0, incremental_engagements) AS incremental_engagements,
	if(incremental_impressions < 0, 0, incremental_impressions) AS incremental_impressions,
	if(incremental_reached < 0, 0, incremental_reached) AS incremental_reached,
	if(incremental_unlikes < 0, 0, incremental_unlikes) AS incremental_unlikes,
	current_timestamp
FROM (
        SELECT openInterval.property_id,
            openInterval.date_id,
            openInterval.date,
            openInterval.total_followers,
            -- computed data: [value at end of open interval]
            --  + [value at end of facebook day, if present]
            --  - [value at end of previous property day, if present]
            (
                openInterval.incremental_likes + coalesce(facebookDay.incremental_likes, 0) - coalesce(propertyDay.
                    incremental_likes, 0)
                ) AS incremental_likes,
            (
                openInterval.incremental_number_of_posts + coalesce(facebookDay.incremental_number_of_posts, 0) - coalesce(
                    propertyDay.incremental_number_of_posts, 0)
                ) AS incremental_number_of_posts,
            (
                openInterval.incremental_engagements + coalesce(facebookDay.incremental_engagements, 0) - coalesce(
                    propertyDay.incremental_engagements, 0)
                ) AS incremental_engagements,
            (
                openInterval.incremental_impressions + coalesce(facebookDay.incremental_impressions, 0) - coalesce(
                    propertyDay.incremental_impressions, 0)
                ) AS incremental_impressions,
            (
                openInterval.incremental_reached + coalesce(facebookDay.incremental_reached, 0) - coalesce(propertyDay.
                    incremental_reached, 0)
                ) AS incremental_reached,
            (
                openInterval.incremental_unlikes + coalesce(facebookDay.incremental_unlikes, 0) - coalesce(propertyDay.
                    incremental_unlikes, 0)
                ) AS incremental_unlikes,
            current_timestamp
        FROM (
            SELECT *
            FROM dp.RawImportedFacebookPageStatistics
            WHERE data_collection_run = 1
--                 AND property_id = 8306
--                 AND property_id = 175
-- 				AND property_id = 6892
                -- Set date since the data are being reloaded
                AND date_id >= 20170401
            ) openInterval
        LEFT JOIN (
            SELECT *
            FROM dp.RawImportedFacebookPageStatistics
            WHERE data_collection_run = 2
--                 AND property_id = #{propertyId}
--                 AND date_id = #{incParams.dateId}
            ) facebookDay ON openInterval.property_id = facebookDay.property_id
            AND openInterval.date_id = facebookDay.date_id
        LEFT JOIN (
            SELECT *
            FROM dp.RawImportedFacebookPageStatistics
            WHERE data_collection_run = 1
--                 AND property_id = #{propertyId}
--                 AND date_id = #{incParams.dateIdPrev}
            ) propertyDay ON openInterval.property_id = propertyDay.property_id
            AND openInterval.date_id - 1 = propertyDay.date_id
) AS ifps
