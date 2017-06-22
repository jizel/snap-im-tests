SELECT date_id, count(DISTINCT property_id) FROM RawImportedFacebookPageStatistics GROUP BY date_id;
SELECT date_id, count(DISTINCT property_id) FROM dp.IncrementalFacebookPageStatistics WHERE date_id >= 20170401 GROUP BY date_id DESC;
SELECT dim_date_id, count(DISTINCT dim_property_id) FROM dp.T_FacebookPageStats WHERE dim_date_id >= 20170401 GROUP BY dim_date_id DESC;
SELECT dim_date_id, count(DISTINCT dim_property_id) FROM dp.FactFacebookPageStats WHERE dim_date_id >= 20170401 GROUP BY dim_date_id ORDER by dim_date_id DESC;
