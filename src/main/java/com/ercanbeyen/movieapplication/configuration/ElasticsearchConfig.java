package com.ercanbeyen.movieapplication.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Slf4j
@EnableElasticsearchRepositories(basePackages = "com.ercanbeyen.movieapplication.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Override
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("127.0.0.1")
                .usingSsl()
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(3))
                .withBasicAuth("elastic", "y*=lLK-oFq2KOCmqYX9-")
                .withHeaders(() -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return headers;
                })
                .build();
        return clientConfiguration;
    }
}