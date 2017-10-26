-- Create incremental data backup table
DROP TABLE dp.Fact_facebook_incremental_bak;
CREATE TABLE dp.Fact_facebook_incremental_bak LIKE dp.Fact_facebook_incremental;
INSERT dp.Fact_facebook_incremental_bak SELECT * FROM dp.Fact_facebook_incremental;


-- Diff incremental data with backup table
SELECT
-- 	r.*
	date_id,
	count(*)
FROM 
	(
		SELECT
			t.*,
			ABS(t.diff_number_of_posts) + ABS(t.diff_impressions) + ABS(t.diff_engagements) + ABS(t.diff_reach) + ABS(t.diff_likes) + ABS(t.diff_unlikes) AS total
		FROM	
			(
				SELECT
					ffi1.property_id,
					ffi1.date_id,
					CAST(ffi1.number_of_posts AS SIGNED) - CAST(ffi2.number_of_posts AS SIGNED) AS diff_number_of_posts,
					CAST(ffi1.impressions AS SIGNED) - CAST(ffi2.impressions AS SIGNED) AS diff_impressions,
					CAST(ffi1.engagements AS SIGNED) - CAST(ffi2.engagements AS SIGNED) diff_engagements,
					CAST(ffi1.reach AS SIGNED) - CAST(ffi2.reach AS SIGNED) AS diff_reach,
					CAST(ffi1.likes AS SIGNED) - CAST(ffi2.likes AS SIGNED) AS diff_likes,
					CAST(ffi1.unlikes AS SIGNED) - CAST(ffi2.unlikes AS SIGNED) AS diff_unlikes
				FROM
					dp.Fact_facebook_incremental ffi1
				INNER JOIN
					dp.Fact_facebook_incremental_bak ffi2
				ON
					ffi1.date_id = ffi2.date_id AND ffi1.property_id = ffi2.property_id
			) AS t
	) AS r
WHERE
	total > 0
GROUP BY
	date_id
;


-- Check data
SELECT 
	property_id, 
	date_id,
	total_followers AS followers,
	incremental_number_of_posts AS posts,
	incremental_engagements AS engagements,
	incremental_impressions AS impressions,
	incremental_reach AS reach,
	incremental_likes AS likes,
	incremental_unlikes AS unlikes,
	datapoint
FROM 
	dp.Fact_facebook_raw
WHERE property_id = 9313 AND date_id IN (20170812, 20170811) ORDER BY date_id, datapoint;

SELECT * FROM dp.Fact_facebook_incremental WHERE property_id = 9313;
SELECT * FROM dp.Fact_facebook_incremental_bak WHERE property_id = 9313;

SELECT count(*) FROM dp.Fact_facebook_raw WHERE
datapoint = 2 AND date_id >= 20170801
AND CONCAT(total_followers, incremental_number_of_posts, incremental_engagements, incremental_impressions, incremental_impressions, incremental_likes, incremental_unlikes) IS NULL;) 


SELECT date_id, count(*) FROM dp.Fact_facebook_raw WHERE
	datapoint = 2 AND date_id >= 20170801
	AND total_followers IS NULL
	AND incremental_number_of_posts IS NULL
	AND incremental_engagements IS NULL
	AND incremental_impressions IS NULL
	AND incremental_reach IS NULL
	AND incremental_likes IS NULL
	AND incremental_unlikes IS NULL
GROUP BY date_id;

SELECT date_id, count(*) FROM dp.Fact_facebook_raw WHERE
	datapoint = 2 AND date_id >= 20170801
	OR total_followers IS NULL
	OR incremental_number_of_posts IS NULL
	OR incremental_engagements IS NULL
	OR incremental_impressions IS NULL
	OR incremental_reach IS NULL
	OR incremental_likes IS NULL
	OR incremental_unlikes IS NULL
GROUP BY date_id;

