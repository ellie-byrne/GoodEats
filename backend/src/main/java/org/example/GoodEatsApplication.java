package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.example")

public class GoodEatsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodEatsApplication.class, args);
    }
}