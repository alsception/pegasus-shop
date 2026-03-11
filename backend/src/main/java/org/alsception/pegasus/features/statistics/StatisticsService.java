package org.alsception.pegasus.features.statistics;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService 
{
    private static final Logger log = LoggerFactory.getLogger(StatisticsService.class);
    private final JdbcTemplate jdbcTemplate;

    public StatisticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getTotalOrders() 
    {
        String sql = 
        """
            SELECT 
                  DATE(created) AS datum, 
                  COUNT(*) AS broj_narudzbina
            FROM pgs_orders
            GROUP BY DATE(created)
            ORDER BY datum ASC""";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getTotalAmount() 
    {
        String sql = 
        """
            SELECT 
                  DATE(created) AS datum, 
                  SUM(price) AS ukupni_promet,
                  COUNT(*) AS broj_narudzbina
            FROM pgs_orders
            GROUP BY DATE(created)
            ORDER BY datum ASC""";
        return jdbcTemplate.queryForList(sql);
    }    

    public List<Map<String, Object>> getTimes() 
    {
        String sql = """
            SELECT 
                code,
                created,
                EXTRACT(EPOCH FROM (u_pripremi_at - created)) / 60 AS cekanje_minuti,
                EXTRACT(EPOCH FROM (spremno_at - u_pripremi_at)) / 60 AS priprema_minuti,
                EXTRACT(EPOCH FROM (spremno_at - created)) / 60 AS ukupno_vreme
            FROM pgs_orders
            WHERE spremno_at IS NOT NULL AND u_pripremi_at IS NOT NULL
            ORDER BY created DESC;
            """;
        return jdbcTemplate.queryForList(sql);
    }
}