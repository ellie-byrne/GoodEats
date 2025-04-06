// src/test/java/org/example/MongoTestConfig.java
package org.example.Config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.mockito.Mockito;

@TestConfiguration
public class MongoTestConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        return Mockito.mock(MongoTemplate.class);
    }
}