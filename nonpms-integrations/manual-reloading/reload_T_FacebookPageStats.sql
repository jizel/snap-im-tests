REPLACE INTO dp.T_FacebookPageStats (
            dim_property_id,
            dim_date_id,
            date,
            impressions,
            engagements,
            followers,
            number_of_posts,
            reach,
            likes,
            unlikes,
            time_stamp
            )
SELECT * FROM
(
        SELECT
        	ifps1.property_id AS property_id,
            ifps1.date_id AS date_id,
            ifps1.date AS date,
			SUM(ifps2.incremental_impressions) AS impressions,
			SUM(ifps2.incremental_engagements) AS engagements,
			ifps1.total_followers AS followers,
			SUM(ifps2.incremental_number_of_posts) AS number_of_posts,
			SUM(ifps2.incremental_reached) AS reach,
            SUM(ifps2.incremental_likes) AS likes,
            SUM(ifps2.incremental_unlikes) AS unlikes,
            ifps1.time_stamp
        FROM
        	(
            SELECT *
            FROM dp.IncrementalFacebookPageStatistics t1
            WHERE t1.time_stamp = (
                    SELECT MAX(time_stamp)
                    FROM dp.IncrementalFacebookPageStatistics tt1
                    WHERE
                    	tt1.property_id = t1.property_id
                        AND tt1.date_id = t1.date_id
                    )
        	) ifps1
        	INNER JOIN
            (
            SELECT *
            FROM dp.IncrementalFacebookPageStatistics t2
            WHERE t2.time_stamp = (
                    SELECT MAX(time_stamp)
                    FROM dp.IncrementalFacebookPageStatistics tt2
                    WHERE
                    	tt2.property_id = t2.property_id
                        AND tt2.date_id = t2.date_id
                    )
            ) ifps2
            WHERE
            	ifps1.date_id >= ifps2.date_id
            	AND ifps1.property_id = ifps2.property_id
--             	AND ifps1.property_id = 8306
                -- Set date since the data are being reloaded
            	AND ifps1.date_id >= 20170421
--             	AND ifps2.date_id >= 20170414
--             	AND ifps2.property_id = 8306
--              ORDER BY ifps1.date_id, ifps2.date_id
 			GROUP BY ifps1.date_id, ifps1.property_id
) AS ifps
