package com.spurs.framework.core.JdbcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcTemplateManager {

    private static Logger logger = LoggerFactory.getLogger(JdbcTemplateManager.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取jdbcTemplate
     * 
     * @return
     */
    public JdbcTemplate getJdbcTemplate(String... name) {
        if (jdbcTemplate == null) {
            logger.error("数据源连接失败，无法获取jdbctemplate");
            return null;
        }
        return jdbcTemplate;
    }

}