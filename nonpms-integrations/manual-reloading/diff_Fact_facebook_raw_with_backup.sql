-- Create raw data backup table
DROP TABLE dp.Fact_facebook_raw_bak;
CREATE TABLE dp.Fact_facebook_raw_bak LIKE dp.Fact_facebook_raw;
INSERT dp.Fact_facebook_raw_bak SELECT * FROM dp.Fact_facebook_raw;


-- Diff raw data with backup table
SELECT
	date_id,
	count(*),
-- 	t.*,
	ABS(t.diff_number_of_posts) + ABS(t.diff_impressions) + ABS(t.diff_engagements) + ABS(t.diff_reach) + ABS(t.diff_likes) + ABS(t.diff_unlikes) AS total
FROM	
	(
		SELECT
			ffi1.property_id,
			ffi1.date_id,
			CAST(ffi1.incremental_number_of_posts AS SIGNED) - CAST(ffi2.incremental_number_of_posts AS SIGNED) AS diff_number_of_posts,
			CAST(ffi1.incremental_impressions AS SIGNED) - CAST(ffi2.incremental_impressions AS SIGNED) AS diff_impressions,
			CAST(ffi1.incremental_engagements AS SIGNED) - CAST(ffi2.incremental_engagements AS SIGNED) diff_engagements,
			CAST(ffi1.incremental_reach AS SIGNED) - CAST(ffi2.incremental_reach AS SIGNED) AS diff_reach,
			CAST(ffi1.incremental_likes AS SIGNED) - CAST(ffi2.incremental_likes AS SIGNED) AS diff_likes,
			CAST(ffi1.incremental_unlikes AS SIGNED) - CAST(ffi2.incremental_unlikes AS SIGNED) AS diff_unlikes
		FROM
			dp.Fact_facebook_raw ffi1
		INNER JOIN
			dp.Fact_facebook_raw_bak ffi2
		ON
			ffi1.date_id = ffi2.date_id AND ffi1.property_id = ffi2.property_id AND ffi1.datapoint = ffi2.datapoint
			-- Set since date
			AND ffi1.date_id >= 20170801
	) AS t	
-- ORDER BY total DESC, date_id DESC, property_id
GROUP BY date_id
;