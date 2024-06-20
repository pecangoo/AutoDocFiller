package com.example.filerspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public com.mongodb.client.MongoClient mongoClient() {
        return com.mongodb
                .client
                .MongoClients
                .create("mongodb://localhost:27017");
    }
}