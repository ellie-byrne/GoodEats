// src/test/java/org/example/TestUtils/TestMongoTemplate.java
package org.example.TestUtils;

import org.example.Models.Counter;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.HashMap;
import java.util.Map;

public class TestMongoTemplate {
    private final Map<String, Counter> counters = new HashMap<>();

    public Counter findAndModify(Query query, Update update, FindAndModifyOptions options, Class<Counter> entityClass) {
        // Simple implementation that increments the counter
        String counterId = "testCounter"; // In a real implementation, we'd extract this from the query

        Counter counter = counters.getOrDefault(counterId, new Counter(counterId, 0));
        counter.setSeq(counter.getSeq() + 1);
        counters.put(counterId, counter);

        return counter;
    }
}