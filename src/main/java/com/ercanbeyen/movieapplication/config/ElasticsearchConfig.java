package com.ercanbeyen.movieapplication.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
@EnableElasticsearchRepositories(basePackages = "com.ercanbeyen.movieapplication.repository")
@PropertySource("classpath:application.properties")
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    private final String username;
    private final String password;
    private final String ip;

    public ElasticsearchConfig(@Value("${elasticsearch.username}") String username, @Value("${elasticsearch.password}") String password, @Value("${application.ip}") String ip) {
        this.username = username;
        this.password = password;
        this.ip = ip;
    }

    @Override
    public @NonNull ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(ip)
                .usingSsl()
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .withBasicAuth(username, password)
                .withHeaders(() -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return headers;
                })
                .build();
    }
}