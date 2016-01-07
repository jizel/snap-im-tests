INSERT INTO OTA_DM.D_country SELECT * FROM OTA_STG.stg_D_country;

INSERT INTO OTA_DM.D_country SELECT * FROM OTA_STG.stg_D_region;

INSERT INTO OTA_DM.D_city SELECT * FROM OTA_STG.stg_D_city;

INSERT INTO OTA_DM.D_hotel  SELECT * FROM OTA_STG.stg_D_hotel;

INSERT INTO OTA_STG.market (d_country, d_region, d_city, district, stars3, stars4, stars5)
SELECT
    c.d_country,
    c.d_region,
    c.id,
    h.district_name,
    SUM(IF(h.stars=3, 1, 0)) AS stars3,
    SUM(IF(h.stars=4, 1, 0)) AS stars4,
    SUM(IF(h.stars=5, 1, 0)) AS stars5
FROM OTA_STG.stg_D_city c JOIN OTA_STG.stg_D_hotel h ON h.d_city=c.id GROUP BY 1,2,3,4;

INSERT INTO OTA_STG.market_by_city (d_country, d_region, d_city, stars3, stars4, stars5)
SELECT
    d_country,
    d_region,
    d_city,
    SUM(stars3),
    SUM(stars4),
    SUM(stars5)
FROM OTA_STG.market GROUP BY 1, 2, 3;

INSERT INTO OTA_STG.market_by_region (d_country, d_region, stars3, stars4, stars5)
SELECT
    d_country,
    d_region ,
    SUM(stars3),
    SUM(stars4),
    SUM(stars5)
FROM OTA_STG.market GROUP BY 1, 2;

INSERT INTO OTA_STG.stg_D_hotel_count (hotel_id, region_count, city_count, district_count)
SELECT 
    h.id,
    CASE
        WHEN h.stars=3 THEN mr.stars3
        WHEN h.stars=4 THEN mr.stars4
        ELSE mr.stars5
    END AS region_sum,
    CASE
        WHEN h.stars=3 THEN mc.stars3
        WHEN h.stars=4 THEN mc.stars4
        ELSE mc.stars5
    END AS city_sum,
    CASE
        WHEN h.stars=3 THEN md.stars3
        WHEN h.stars=4 THEN md.stars4
        ELSE md.stars5
    END AS district_sum
FROM OTA_STG.stg_D_hotel h
JOIN OTA_STG.stg_D_city c ON c.id=h.d_city
JOIN OTA_STG.market_by_region mr ON mr.d_region=c.d_region
JOIN OTA_STG.market_by_city mc ON mc.d_city=h.d_city
JOIN OTA_STG.market md ON md.d_city=h.d_city AND md.district=h.district_name
GROUP BY 1;
