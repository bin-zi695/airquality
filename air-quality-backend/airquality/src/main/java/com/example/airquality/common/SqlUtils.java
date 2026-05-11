package com.example.airquality.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqlUtils {

    private static final Logger log = LoggerFactory.getLogger(SqlUtils.class);
    private final JdbcTemplate jdbcTemplate;

    public SqlUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void resetAutoIncrement(String tableName, long nextId) {
        String sql = "ALTER TABLE " + tableName + " AUTO_INCREMENT = " + nextId;
        jdbcTemplate.execute(sql);
        log.debug("重置 {} AUTO_INCREMENT = {}", tableName, nextId);
    }

    public void resetUserAutoIncrement(long nextId) {
        resetAutoIncrement("user", nextId);
    }

    public void resetCityAutoIncrement(long nextId) {
        resetAutoIncrement("city", nextId);
    }

    public void resetAirDataAutoIncrement(long nextId) {
        resetAutoIncrement("air_quality_data", nextId);
    }

    public void resetArticleAutoIncrement(long nextId) {
        resetAutoIncrement("air_quality_article", nextId);
    }

    public void resetAlertThresholdAutoIncrement(long nextId) {
        resetAutoIncrement("alert_threshold", nextId);
    }
}
