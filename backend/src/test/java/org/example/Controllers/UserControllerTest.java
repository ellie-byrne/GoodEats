// src/test/java/org/example/UserControllerTest.java
package org.example.Controllers;

import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.example.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<User> response = userController.getUserById(1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser, response.getBody());
    }

    @Test
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserById(999);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void signUp_Success() {
        // Arrange
        when(userService.signUp(any(User.class))).thenReturn("User signed up successfully!");

        // Act
        ResponseEntity<Map<String, String>> response = userController.signUp(testUser);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User signed up successfully!", response.getBody().get("message"));
    }

    @Test
    void signUp_MissingFields() {
        // Arrange
        User incompleteUser = new User();
        // Missing all fields

        // Act
        ResponseEntity<Map<String, String>> response = userController.signUp(incompleteUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("All fields are required.", response.getBody().get("message"));
    }

    @Test
    void signUp_UsernameExists() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Map<String, String>> response = userController.signUp(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists.", response.getBody().get("message"));
    }

    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.login(testUser);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody().get("message"));
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = userController.login(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username not found", response.getBody().get("message"));
    }

    @Test
    void login_IncorrectPassword() {
        // Arrange
        User storedUser = new User();
        storedUser.setUsername("testuser");
        storedUser.setPassword("differentpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(storedUser));

        // Act
        ResponseEntity<Map<String, Object>> response = userController.login(testUser);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody().get("message"));
    }
}