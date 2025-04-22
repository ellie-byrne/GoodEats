// src/test/java/org/example/UserIntegrationTest.java
package org.example.Integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.example.Services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;

    @Test
    void getUserById() throws Exception {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        // Don't include password in response

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert - Success case
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        // Act & Assert - Not found case
        mockMvc.perform(get("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void signUp() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password123");
        user.setEmail("new@example.com");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        User mockUser = new User(1, "mockuser", "pass123", "mock@example.com");
        when(userService.signUp(any(User.class))).thenReturn(mockUser);

        // Act & Assert - Success case
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User signed up successfully!")));

        // Arrange for username exists case
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));
        user.setUsername("existinguser");

        // Act & Assert - Username exists case
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username already exists.")));
    }

    @Test
    void login() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        User storedUser = new User();
        storedUser.setUsername("testuser");
        storedUser.setPassword("password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(storedUser));
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert - Success case
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Login successful")));

        // Act & Assert - User not found case
        user.setUsername("nonexistent");
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username not found")));

        // Act & Assert - Wrong password case
        user.setUsername("testuser");
        user.setPassword("wrongpassword");
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Incorrect password")));
    }
}
