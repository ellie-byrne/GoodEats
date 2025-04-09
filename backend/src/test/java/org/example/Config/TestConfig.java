// src/test/java/org/example/Config/TestConfig.java
package org.example.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    @Bean
    public MongodConfig mongodConfig() throws Exception {
        return MongodConfig.builder()
                .version(Version.Main.V4_0)
                .net(new Net(27017, Network.localhostIsIPv6()))
                .build();
    }

    @Bean
    public MongodExecutable mongodExecutable(MongodConfig mongodConfig) {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        return starter.prepare(mongodConfig);
    }

    @Bean
    public MongoClient mongoClient(MongodExecutable mongodExecutable) throws IOException {
        mongodExecutable.start();
        return MongoClients.create(CONNECTION_STRING);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "test");
    }
}