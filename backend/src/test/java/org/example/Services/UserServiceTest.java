package org.example.Services;

import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.example.Services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(Counter.class))).thenReturn(testCounter);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.signUp(testUser);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_MissingFields() {
        User incompleteUser = new User();
        incompleteUser.setUsername("testuser"); // Missing password and email

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signUp(incompleteUser);
        });

        assertEquals("All fields are required!", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUp_UsernameExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signUp(testUser);
        });

        assertEquals("Username is already taken.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUp_EmailExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signUp(testUser);
        });

        assertEquals("Email is already taken.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        String result = userService.login("testuser", "password123");

        assertEquals("Login successful!", result);
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        String result = userService.login("nonexistent", "password123");

        assertEquals("Username does not exist.", result);
    }

    @Test
    void login_IncorrectPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        String result = userService.login("testuser", "wrongpassword");

        assertEquals("Incorrect password.", result);
    }

    @Test
    void getNextSequence_Success() throws Exception {
        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class),
                any(FindAndModifyOptions.class), eq(Counter.class))).thenReturn(testCounter);

        java.lang.reflect.Method method = UserService.class.getDeclaredMethod("getNextSequence", String.class);
        method.setAccessible(true);
        int result = (int) method.invoke(userService, "userId");

        assertEquals(1, result);
    }
}