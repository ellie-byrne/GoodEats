// src/test/java/org/example/TestConfig.java
package org.example;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {

    @Bean
    public MongoTemplate mongoTemplate() {
        return Mockito.mock(MongoTemplate.class);
    }
}
