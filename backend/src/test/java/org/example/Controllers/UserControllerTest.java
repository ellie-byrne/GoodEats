package org.example.Controllers;

import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.example.Services.UserService;
import org.example.DTOs.UserDTO;
import org.example.DTOs.CreateUserRequest;
import org.example.DTOs.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        ResponseEntity<UserDTO> response = userController.getUserById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUser.getUsername(), response.getBody().getUsername());
        assertEquals(testUser.getEmail(), response.getBody().getEmail());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = userController.getUserById(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void signUp_Success() {
        when(userService.signUp(any(User.class))).thenReturn(testUser);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        ResponseEntity<UserDTO> response = userController.signUp(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void signUp_MissingFields() {
        CreateUserRequest emptyRequest = new CreateUserRequest();
        // Explicitly set fields to null or empty
        emptyRequest.setUsername(null);
        emptyRequest.setPassword(null);
        emptyRequest.setEmail(null);

        ResponseEntity<UserDTO> response = userController.signUp(emptyRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, never()).signUp(any(User.class));
    }

    @Test
    void login_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        LoginRequest login = new LoginRequest();
        login.setUsername("testuser");
        login.setPassword("password123");

        ResponseEntity<Map<String, Object>> response = userController.login(login);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody().get("message"));
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        LoginRequest login = new LoginRequest();
        login.setUsername("notfound");
        login.setPassword("irrelevant");

        ResponseEntity<Map<String, Object>> response = userController.login(login);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username not found", response.getBody().get("message"));
    }

    @Test
    void login_IncorrectPassword() {
        User storedUser = new User();
        storedUser.setUsername("testuser");
        storedUser.setPassword("wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(storedUser));

        LoginRequest login = new LoginRequest();
        login.setUsername("testuser");
        login.setPassword("password123");

        ResponseEntity<Map<String, Object>> response = userController.login(login);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody().get("message"));
    }
}