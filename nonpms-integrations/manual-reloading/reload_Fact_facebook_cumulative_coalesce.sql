-- Create backup table
DROP TABLE dp.Fact_facebook_cumulative_bak;
CREATE TABLE dp.Fact_facebook_cumulative_bak LIKE dp.Fact_facebook_cumulative;
INSERT dp.Fact_facebook_cumulative_bak SELECT * FROM dp.Fact_facebook_cumulative;

-- Recompute cumulative data
		REPLACE INTO
            dp.Fact_facebook_cumulative_bak
        SELECT
            ifps1.property_id,
            ifps1.date_id,
            max(coalesce(ifps1.total_followers, 0)) AS followers,
            sum(coalesce(ifps2.number_of_posts, 0)) AS number_of_posts,
            sum(coalesce(ifps2.engagements, 0)) AS engagements,
            sum(coalesce(ifps2.impressions, 0)) AS impressions,
            sum(coalesce(ifps2.reach, 0)) AS reach,
            sum(coalesce(ifps2.likes, 0)) AS likes,
            sum(coalesce(ifps2.unlikes, 0)) AS unlikes,
            ifps1.time_stamp,
            current_timestamp
        FROM
            dp.Fact_facebook_incremental ifps1
        JOIN
            dp.Fact_facebook_incremental ifps2
            ON ifps1.property_id = ifps2.property_id AND ifps1.date_id >= ifps2.date_id
        GROUP BY ifps1.property_id, ifps1.date_id