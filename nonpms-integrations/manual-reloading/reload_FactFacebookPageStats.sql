        REPLACE INTO dp.FactFacebookPageStats (
            dim_property_id,
            dim_date_id,
            impressions,
            engagements,
            followers,
            number_of_posts,
            reach,
            likes,
            unlikes,
            collected_time_stamp,
            inserted_time_stamp
            )
        SELECT t.dim_property_id,
            t.dim_date_id,
            COALESCE(t.impressions, 0),
            COALESCE(t.engagements, 0),
            COALESCE(t.followers, 0),
            COALESCE(t.number_of_posts, 0),
            COALESCE(t.reach, 0),
            COALESCE(t.likes, 0),
            COALESCE(t.unlikes, 0),
            t.time_stamp,
            current_timestamp
        FROM dp.T_FacebookPageStats t
        WHERE time_stamp = (
                SELECT MAX(time_stamp)
                FROM dp.T_FacebookPageStats tt
                WHERE tt.dim_property_id = t.dim_property_id
                    AND tt.dim_date_id = t.dim_date_id
                    -- Set date since the data are being reloaded
                    AND t.dim_date_id >= 20170421
                )
--             AND t.date = (#{date})
-- 			AND t.dim_date_id >= 20140414
--             AND t.dim_property_id = 8306
