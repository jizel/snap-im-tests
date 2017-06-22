SELECT
    d.property_id,
    d.property_key,
    d.date_id
FROM
    (
        SELECT
            p.property_id property_id,
            p.property_key property_key,
            d_i.date_id date_id
        FROM
            dp.Dim_date d_i
            INNER JOIN (
                SELECT
                    p_i.property_id property_id,
                    p_i.property_key property_key,
                    (SELECT min(psi.dim_date_id) FROM dp.FactFacebookPageStats psi WHERE psi.dim_property_id = p_i.property_id) min_date_id
                FROM
                    dp.Dim_property p_i
                WHERE
                    EXISTS (SELECT psi.dim_date_id FROM dp.FactFacebookPageStats psi WHERE psi.dim_property_id = p_i.property_id)
            ) p ON d_i.date_id >= 20160801 AND d_i.date_id >= p.min_date_id  AND d_i.date_id <= CAST(DATE_FORMAT(CURDATE(), '%Y%m%d') AS int)
    ) d
    LEFT JOIN dp.FactFacebookPageStats ps ON (ps.dim_date_id = d.date_id AND ps.dim_property_id = d.property_id)
WHERE
    ps.dim_date_id IS NULL
ORDER BY
    d.property_id,
    d.date_id
