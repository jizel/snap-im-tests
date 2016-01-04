SET @stamp_now = CURRENT_TIMESTAMP;

-- Regional level
INSERT INTO OTA_DM.F_market_snap (
    stamp,
    city_load_id,
    id,
    hotel_id
)
SELECT
    @stamp_now,
    c.city_load_id,
    hc.hotel_id,
    h2.id
FROM OTA_STG.stg_D_hotel_count hc
JOIN OTA_STG.stg_D_hotel h ON h.id=hc.hotel_id
JOIN OTA_STG.stg_D_city c ON c.id=h.d_city
JOIN OTA_STG.stg_D_city c2 ON c2.d_region=c.d_region AND hc.city_count<10
JOIN OTA_STG.stg_D_hotel h2 ON h2.stars=h.stars AND h2.d_city=c2.id AND h2.id<>h.id
WHERE hc.region_count>9
;

-- City level
INSERT INTO OTA_DM.F_market_snap (
    stamp,
    city_load_id,
    id,
    hotel_id
)
SELECT
    @stamp_now,
    c.city_load_id,
    hc.hotel_id,
    h2.id
FROM OTA_STG.stg_D_hotel_count hc
JOIN OTA_STG.stg_D_hotel h ON h.id=hc.hotel_id
JOIN OTA_STG.stg_D_city c ON c.id=h.d_city
JOIN OTA_STG.stg_D_hotel h2 ON h2.stars=h.stars AND h2.d_city=h.d_city AND hc.district_count<10 AND hc.city_count>9 AND h2.id<>h.id
;

-- District level
INSERT INTO OTA_DM.F_market_snap (
    stamp,
    city_load_id,
    id,
    hotel_id
)
SELECT
    @stamp_now,
    c.city_load_id,
    hc.hotel_id,
    h2.id
FROM OTA_STG.stg_D_hotel_count hc
JOIN OTA_STG.stg_D_hotel h ON h.id=hc.hotel_id
JOIN OTA_STG.stg_D_city c ON c.id=h.d_city
JOIN OTA_STG.stg_D_hotel h2 ON h2.stars=h.stars AND h2.d_city=h.d_city AND h2.district_name=h.district_name AND hc.district_count>9 AND h2.id<>h.id
;