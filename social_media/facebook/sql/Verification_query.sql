SELECT ifps.property_id,
       ifps.date,
       ifps.incremental_impressions,
       ffps.impressions,
       ifps.incremental_engagements,
       ffps.engagements,
       ifps.total_followers,
       ffps.followers,
       ifps.incremental_reached,
       ffps.reach,
       ifps.incremental_likes,
       ffps.likes,
       ifps.incremental_unlikes,
       ffps.unlikes,
       ifps.time_stamp
FROM DP_FB_PAGE_STATS_InnoDB.ImportedFacebookPageStatistics ifps 
LEFT OUTER JOIN   
     DP_FB_PAGE_STATS_InnoDB.FactFacebookPageStats ffps 
  ON ffps.dim_property_id = ifps.property_id  
 AND ffps.dim_date_id = ifps.date_id;
 
-- A WHERE clause can be added at the end to verify for a specific property_id value
-- For each property_id the following checks need to be made:
--
-- For incremental values:
-- For each date, the total value is equal to the sum of the total value from the previous day and the incremental value from the current day
-- 
-- For total values:
-- the values from both tables are equal
--
-- Note that currently only the statiscits for "followers" list total values. The rest of the values are incremental.
