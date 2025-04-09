package org.example.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CounterTest {

    @Test
    public void testCounterConstructorAndGetters() {
        // Arrange & Act
        Counter counter = new Counter("userId", 10);

        // Assert
        assertEquals("userId", counter.getId());
        assertEquals(10, counter.getSeq());
    }

    @Test
    public void testCounterSetters() {
        // Arrange
        Counter counter = new Counter();

        // Act
        counter.setId("reviewId");
        counter.setSeq(5);

        // Assert
        assertEquals("reviewId", counter.getId());
        assertEquals(5, counter.getSeq());
    }

    @Test
    public void testEmptyConstructor() {
        // Arrange & Act
        Counter counter = new Counter();

        // Assert
        assertNull(counter.getId());
        assertEquals(0, counter.getSeq());
    }
}
