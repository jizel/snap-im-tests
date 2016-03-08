SET SESSION SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/common/dp.Dim_property.tsv' INTO TABLE dp.Dim_property FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/facebook/dp.FactFacebookPageStats.tsv' INTO TABLE dp.FactFacebookPageStats FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/facebook/dp.Fact_fb_post_stats.tsv' INTO TABLE dp.Fact_fb_post_stats FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/instagram/dp.Fact_instagram_daily.tsv' INTO TABLE dp.Fact_instagram_daily FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/twitter/dp.Fact_twitter_daily.tsv' INTO TABLE dp.Fact_twitter_daily FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/twitter/dp.Fact_twitter_tweets.tsv' INTO TABLE dp.Fact_twitter_tweets FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/review/dp.Fact_ta_daily.tsv' INTO TABLE dp.Fact_ta_daily FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/review/dp.Fact_ta_trip_type.tsv' INTO TABLE dp.Fact_ta_trip_type FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/review/dp.tripadvisor_geo_location.tsv' INTO TABLE dp.tripadvisor_geo_location FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/review/dp.tripadvisor_property.tsv' INTO TABLE dp.tripadvisor_property FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/web_performance/dp.Dim_wp_source.tsv' INTO TABLE dp.Dim_wp_source FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/web_performance/dp.Fact_web_performance_cumulative.tsv' INTO TABLE dp.Fact_web_performance_cumulative FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/web_performance/dp.Fact_web_performance.tsv' INTO TABLE dp.Fact_web_performance FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/ota/OTA_STG.stg_D_country.tsv' INTO TABLE OTA_STG.stg_D_country FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/ota/OTA_STG.stg_D_region.tsv' INTO TABLE OTA_STG.stg_D_region FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/ota/OTA_STG.stg_D_city.tsv' INTO TABLE OTA_STG.stg_D_city FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/ota/OTA_STG.stg_D_hotel.tsv' INTO TABLE OTA_STG.stg_D_hotel FIELDS TERMINATED BY '\t';
LOAD DATA LOCAL INFILE '{path}/fake-tsv-data/ota/OTA_DM.F_min_rate.tsv' INTO TABLE OTA_DM.F_min_rate FIELDS TERMINATED BY '\t';