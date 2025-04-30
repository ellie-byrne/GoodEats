// File: src/main/java/org/example/Controllers/UserController.java
package org.example.Controllers;

import org.example.DTOs.CreateUserRequest;
import org.example.DTOs.LoginRequest;
import org.example.DTOs.UserDTO;
import org.example.Factories.UserFactory;
import org.example.Mappers.UserMapper;
import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.example.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserFactory userFactory;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, UserMapper userMapper, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.userFactory = userFactory;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<?> getUserFavorites(@PathVariable Integer id) {
        // In a microservices architecture, we would call the review service API
        // to get the user's favorite restaurants
        return ResponseEntity.ok().body("This endpoint would call the review service API to get user favorites");
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        User user = userFactory.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getEmail()
        );
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request) {
        return userService.authenticateUser(request.getUsername(), request.getPassword())
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}