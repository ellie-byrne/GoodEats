// src/test/java/org/example/Services/UserServiceTest.java
package org.example.Services;

import org.example.Models.User;
import org.example.TestUtils.TestMongoTemplate;
import org.example.TestUtils.TestUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceTest {
    // Test code remains the same but with updated imports
    private UserService userService;
    private TestUserRepository userRepository;
    private TestMongoTemplate mongoTemplate;

    @BeforeEach
    public void setup() {
        userRepository = new TestUserRepository();
        mongoTemplate = new TestMongoTemplate();

        // Create a custom UserService that uses our test implementations
        userService = new UserService();

        // We need to set the repository and mongoTemplate fields in UserService
        // This would normally be done by Spring's dependency injection
        // For testing without Mockito, we'll use reflection to set these fields
        try {
            java.lang.reflect.Field repoField = UserService.class.getDeclaredField("userRepository");
            repoField.setAccessible(true);
            repoField.set(userService, userRepository);

            java.lang.reflect.Field mongoField = UserService.class.getDeclaredField("mongoTemplate");
            mongoField.setAccessible(true);
            mongoField.set(userService, mongoTemplate);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testSignUp_Success() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        // Act
        String result = userService.signUp(user);

        // Assert
        assertEquals("User signed up successfully!", result);
        assertNotNull(user.getId());
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testSignUp_MissingFields() {
        // Arrange
        User incompleteUser = new User();
        incompleteUser.setUsername("testuser");
        // Missing password and email

        // Act
        String result = userService.signUp(incompleteUser);

        // Assert
        assertEquals("All fields are required!", result);
        assertEquals(0, userRepository.count());
    }

    @Test
    public void testSignUp_DuplicateUsername() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("testuser");
        existingUser.setPassword("password123");
        existingUser.setEmail("existing@example.com");
        userRepository.save(existingUser);

        User newUser = new User();
        newUser.setUsername("testuser"); // Same username
        newUser.setPassword("password456");
        newUser.setEmail("new@example.com");

        // Act
        String result = userService.signUp(newUser);

        // Assert
        assertEquals("Username is already taken.", result);
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testSignUp_DuplicateEmail() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setUsername("existinguser");
        existingUser.setPassword("password123");
        existingUser.setEmail("test@example.com");
        userRepository.save(existingUser);

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password456");
        newUser.setEmail("test@example.com"); // Same email

        // Act
        String result = userService.signUp(newUser);

        // Assert
        assertEquals("Email is already taken.", result);
        assertEquals(1, userRepository.count());
    }

    @Test
    public void testLogin_Success() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        userRepository.save(user);

        // Act
        String result = userService.login("testuser", "password123");

        // Assert
        assertEquals("Login successful!", result);
    }

    @Test
    public void testLogin_UserNotFound() {
        // Act
        String result = userService.login("nonexistent", "password123");

        // Assert
        assertEquals("Username does not exist.", result);
    }

    @Test
    public void testLogin_IncorrectPassword() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        userRepository.save(user);

        // Act
        String result = userService.login("testuser", "wrongpassword");

        // Assert
        assertEquals("Incorrect password.", result);
    }
}