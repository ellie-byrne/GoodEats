// src/test/java/org/example/UserServiceTest.java
package org.example.Services;

import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Counter testCounter;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");

        testCounter = new Counter();
        testCounter.setId("userId");
        testCounter.setSeq(1);
    }

    @Test
    void signUp_Success() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(Counter.class))).thenReturn(testCounter);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        String result = userService.signUp(testUser);

        // Assert
        assertEquals("User signed up successfully!", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_MissingFields() {
        // Arrange
        User incompleteUser = new User();
        incompleteUser.setUsername("testuser");
        // Missing password and email

        // Act
        String result = userService.signUp(incompleteUser);

        // Assert
        assertEquals("All fields are required!", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUp_UsernameExists() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        // Act
        String result = userService.signUp(testUser);

        // Assert
        assertEquals("Username is already taken.", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUp_EmailExists() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act
        String result = userService.signUp(testUser);

        // Assert
        assertEquals("Email is already taken.", result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        String result = userService.login("testuser", "password123");

        // Assert
        assertEquals("Login successful!", result);
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        String result = userService.login("nonexistent", "password123");

        // Assert
        assertEquals("Username does not exist.", result);
    }

    @Test
    void login_IncorrectPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        String result = userService.login("testuser", "wrongpassword");

        // Assert
        assertEquals("Incorrect password.", result);
    }

    @Test
    void getNextSequence_Success() {
        // Arrange
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(Counter.class))).thenReturn(testCounter);

        // Act - Using reflection to test private method
        try {
            java.lang.reflect.Method method = UserService.class.getDeclaredMethod("getNextSequence", String.class);
            method.setAccessible(true);
            int result = (int) method.invoke(userService, "userId");

            // Assert
            assertEquals(1, result);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
