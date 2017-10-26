SELECT date_id, count(date_id) FROM
	(
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
		                    (SELECT min(psi.date_id) FROM dp.Fact_facebook_cumulative psi WHERE psi.property_id = p_i.property_id) min_date_id
		                FROM
		                    dp.Dim_property p_i
		                WHERE
		                    EXISTS (SELECT psi.date_id FROM dp.Fact_facebook_cumulative psi WHERE psi.property_id = p_i.property_id)
		            ) p ON d_i.date_id >= 20160101 AND d_i.date_id >= p.min_date_id  AND d_i.date_id <= CAST(DATE_FORMAT(CURDATE(), '%Y%m%d') AS int)
		    ) d
		    LEFT JOIN dp.Fact_facebook_cumulative ps ON (ps.date_id = d.date_id AND ps.property_id = d.property_id)
		WHERE
		    ps.date_id IS NULL
		ORDER BY
		    d.property_id,
		    d.date_id
	) AS t
GROUP BY t.date_id; 