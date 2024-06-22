package com.example.filerspring.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Value("${mongo.host}")
    String mongoHost;

    @Bean
    public com.mongodb.client.MongoClient mongoClient() {
        return com.mongodb
                .client
                .MongoClients
                .create(mongoHost);
    }
}