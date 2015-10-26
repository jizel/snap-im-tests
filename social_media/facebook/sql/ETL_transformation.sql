INSERT INTO DP_FB_PAGE_STATS_InnoDB.FactFacebookPageStats 
(
  dim_property_id,
  dim_date_id ,
  date,
  impressions,
  engagements,
  followers,
  reach,
  likes,
  unlikes,
  time_stamp    
)
SELECT ifps.property_id, 
       ifps.date_id,
       ifps.date,
       ifps.incremental_impressions + coalesce(ffps.impressions,0), 
       ifps.incremental_engagements + coalesce(ffps.engagements,0), 
       ifps.total_followers, 
       ifps.incremental_reached + coalesce(ffps.reach,0), 
       ifps.total_likes,
       ifps.incremental_unlikes + coalesce(ffps.unlikes,0), 
       ifps.time_stamp
FROM DP_FB_PAGE_STATS_InnoDB.ImportedFacebookPageStatistics ifps 
LEFT OUTER JOIN   
     DP_FB_PAGE_STATS_InnoDB.FactFacebookPageStats ffps 
  ON ffps.dim_property_id = ifps.property_id  
 AND ffps.dim_date_id = ifps.date_id - 1
WHERE ifps.date = curdate()

-- This is the ETL transformation for facebook statistics. 
--
-- F_ImportedFacebookPageStatistics - staging table
-- FactFacebookPageStats - fact table
--
-- aliases:
-- ifps = ImportedFacebookPageStatistics
-- ffps = FactFacebookPageStats