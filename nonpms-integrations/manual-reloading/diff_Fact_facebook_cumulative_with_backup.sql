-- Create cumulative data backup table
DROP TABLE dp.Fact_facebook_cumulative_bak;
CREATE TABLE dp.Fact_facebook_cumulative_bak LIKE dp.Fact_facebook_cumulative;
INSERT dp.Fact_facebook_cumulative_bak SELECT * FROM dp.Fact_facebook_cumulative;


-- Diff cumulative data with backup table
SELECT
-- 	date_id,
-- 	count(*),
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
			dp.Fact_facebook_cumulative ffi1
		INNER JOIN
			dp.Fact_facebook_cumulative_bak ffi2
		ON
			ffi1.date_id = ffi2.date_id AND ffi1.property_id = ffi2.property_id
			-- Set since date
			AND ffi1.date_id = 20170920
	) AS t	
ORDER BY total DESC, date_id DESC, property_id
-- GROUP BY date_id
;