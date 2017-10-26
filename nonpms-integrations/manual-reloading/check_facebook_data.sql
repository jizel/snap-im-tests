SELECT max(date_id), COUNT(property_id) FROM dp.Fact_facebook_raw GROUP BY date_id;
SELECT max(date_id), COUNT(property_id) FROM dp.Fact_facebook_incremental GROUP BY date_id;
SELECT max(date_id), COUNT(property_id) FROM dp.Fact_facebook_cumulative GROUP BY date_id;
