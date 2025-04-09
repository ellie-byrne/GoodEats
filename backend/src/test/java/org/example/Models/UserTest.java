// src/test/java/org/example/Models/UserTest.java
package org.example.Models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        // Arrange & Act
        User user = new User(1, "testuser", "password123", "test@example.com");

        // Assert
        assertEquals(Integer.valueOf(1), user.getId()); // Changed to Integer.valueOf(1)
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void testUserSetters() {
        // Arrange
        User user = new User();

        // Act
        user.setId(2);
        user.setUsername("newuser");
        user.setPassword("newpassword");
        user.setEmail("new@example.com");

        // Assert
        assertEquals(Integer.valueOf(2), user.getId()); // Changed to Integer.valueOf(2)
        assertEquals("newuser", user.getUsername());
        assertEquals("newpassword", user.getPassword());
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    public void testEmptyConstructor() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNull(user.getId()); // Changed to assertNull
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
    }
}