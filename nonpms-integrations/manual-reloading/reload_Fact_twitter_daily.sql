REPLACE INTO
    Fact_twitter_daily
SELECT
    f1.property_id,
    d.date_id,
    ROUND((CAST(f2.impressions AS SIGNED) - CAST(f1.impressions AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.impressions,
    ROUND((CAST(f2.engagement AS SIGNED) - CAST(f1.engagement AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.engagement,
    ROUND((CAST(f2.followers AS SIGNED) - CAST(f1.followers AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.followers,
    ROUND((CAST(f2.number_of_tweets AS SIGNED) - CAST(f1.number_of_tweets AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.number_of_tweets,
    ROUND((CAST(f2.reach AS SIGNED) - CAST(f1.reach AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.reach,
    ROUND((CAST(f2.retweets AS SIGNED) - CAST(f1.retweets AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.retweets,
    ROUND((CAST(f2.retweet_reach AS SIGNED) - CAST(f1.retweet_reach AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.retweet_reach,
    ROUND((CAST(f2.mentions AS SIGNED) - CAST(f1.mentions AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.mentions,
    ROUND((CAST(f2.mention_reach AS SIGNED) - CAST(f1.mention_reach AS SIGNED)) * TIMESTAMPDIFF(DAY, d.day, d1.day) / TIMESTAMPDIFF(DAY, d2.day, d1.day)) + f1.mention_reach
FROM
    Fact_twitter_daily f1
    JOIN Fact_twitter_daily f2 
        ON (f2.property_id = f1.property_id AND f2.date_id = (SELECT MIN(date_id) FROM Fact_twitter_daily WHERE date_id > f1.date_id AND property_id = f1.property_id))
    RIGHT JOIN Dim_date d
        ON (d.date_id > f1.date_id AND d.date_id < f2.date_id)
    JOIN Dim_date d1
        ON (d1.date_id = f1.date_id)
    JOIN Dim_date d2
        ON (d2.date_id = f2.date_id)
WHERE
    TIMESTAMPDIFF(DAY, d1.day, d2.day) > 1
    -- Set SINCE
    AND f1.date_id >= 20171001
    -- Set UNTIL
    AND f1.date_id <= 20171130             
;           

