// src/test/java/org/example/Controllers/UserControllerTest.java
package org.example.Controllers;

import org.example.Models.User;
import org.example.Repositories.UserRepository;
import org.example.Services.UserService;
import org.example.TestUtils.TestUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserControllerTest {

    private UserController userController;
    private TestUserRepository userRepository;
    private TestUserService userService;

    @BeforeEach
    public void setup() {
        userRepository = new TestUserRepository();
        userService = new TestUserService();
        userController = new UserController();

        // Set the repository and service using reflection
        try {
            java.lang.reflect.Field repoField = UserController.class.getDeclaredField("userRepository");
            repoField.setAccessible(true);
            repoField.set(userController, userRepository);

            java.lang.reflect.Field serviceField = UserController.class.getDeclaredField("userService");
            serviceField.setAccessible(true);
            serviceField.set(userController, userService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test", e);
        }
    }

    @Test
    public void testGetUserById_Success() {
        // Arrange
        User user = new User(1, "testuser", "password123", "test@example.com");
        userRepository.save(user);

        // Act
        ResponseEntity<User> response = userController.getUserById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testuser", response.getBody().getUsername());
    }

    @Test
    public void testGetUserById_NotFound() {
        // Act
        ResponseEntity<User> response = userController.getUserById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSignUp_Success() {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password123");
        user.setEmail("new@example.com");

        userService.setSignUpResponse("User signed up successfully!"); // Fixed the expected response

        // Act
        ResponseEntity<Map<String, String>> response = userController.signUp(user);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User signed up successfully!", response.getBody().get("message"));
    }

    @Test
    public void testSignUp_MissingFields() {
        // Arrange
        User incompleteUser = new User();
        incompleteUser.setUsername("testuser");
        // Missing password and email

        userService.setSignUpResponse("All fields are required.");

        // Act
        ResponseEntity<Map<String, String>> response = userController.signUp(incompleteUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("All fields are required.", response.getBody().get("message"));
    }

    @Test
    public void testSignUp_DuplicateUsername() {
        // Arrange
        User existingUser = new User(1, "testuser", "password123", "existing@example.com");
        userRepository.save(existingUser);

        User user = new User();
        user.setUsername("testuser"); // Same username as existing user
        user.setPassword("password123");
        user.setEmail("test@example.com");

        userService.setSignUpResponse("Username is already taken."); // Set the expected response
        userService.setSignUpStatus(HttpStatus.BAD_REQUEST); // Set the expected status

        // Act
        ResponseEntity<Map<String, String>> response = userController.signUp(user);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is already taken.", response.getBody().get("message"));
    }

    // Other test methods remain the same

    // Test service implementation with additional control
    private static class TestUserService extends UserService {
        private String signUpResponse = "User signed up successfully!";
        private HttpStatus signUpStatus = HttpStatus.CREATED;

        public void setSignUpResponse(String response) {
            this.signUpResponse = response;
        }

        public void setSignUpStatus(HttpStatus status) {
            this.signUpStatus = status;
        }

        @Override
        public String signUp(User user) {
            return signUpResponse;
        }
    }

    // Rest of the class remains the same
}