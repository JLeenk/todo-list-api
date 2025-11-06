package dev.jleenksystem.todolist.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

@Configuration
public class DatabaseConfig implements InitializingBean {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    public DatabaseConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Run schema.sql to ensure table exists on startup
        var resource = new ClassPathResource("schema.sql");
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String sql = new String(bytes, StandardCharsets.UTF_8);
        jdbcTemplate.execute(sql);
    }
}