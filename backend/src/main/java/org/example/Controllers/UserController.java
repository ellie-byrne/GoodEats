package org.example.Controllers;

import org.example.Models.User;
import org.example.Respositories.RestaurantRepository;
import org.example.Respositories.ReviewRepository;
import org.example.Respositories.UserRepository;
import org.example.Services.UserService;
import org.example.DTOs.UserDTO;
import org.example.DTOs.CreateUserRequest;
import org.example.DTOs.LoginRequest;
import org.example.Mappers.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    public UserController(UserRepository userRepository,
                          UserService userService,
                          ReviewRepository reviewRepository,
                          RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> ResponseEntity.ok(UserMapper.toDTO(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signUp(@RequestBody CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty() ||
                request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            User user = new User(null, request.getUsername(), request.getPassword(), request.getEmail());
            User savedUser = userService.signUp(user);
            UserDTO dto = UserMapper.toDTO(savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            response.put("message", "Username and password are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String loginResult = userService.login(request.getUsername(), request.getPassword());

        if (loginResult.equals("Login successful")) {
            Optional<User> user = userRepository.findByUsername(request.getUsername());
            response.put("message", loginResult);
            response.put("userId", user.get().getId());
            return ResponseEntity.ok(response);
        }

        response.put("message", loginResult);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}