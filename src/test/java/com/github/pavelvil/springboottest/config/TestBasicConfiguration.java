package com.github.pavelvil.springboottest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.security.SecureRandom;

@Configuration
@PropertySource(value = "file:src/test/resources/application.properties")
public class TestBasicConfiguration {

    @Value("#{environment['spring.datasource.url']}")
    private String jdbcUrl;

    @Value("#{environment['spring.datasource.username']}")
    private String username;

    @Value("#{environment['spring.datasource.password']}")
    private String password;

    @Value("#{environment['spring.datasource.driver-class-name']}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().driverClassName(driverClassName)
            .url(jdbcUrl)
            .username(username)
            .password(password)
            .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom("salt".getBytes()));
    }

}
