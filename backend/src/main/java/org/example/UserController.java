package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}/favourites")
    public List<Restaurant> getUserFavourites(@PathVariable int userId) {
        List<Review> favReviews = reviewRepository.findByUserIDAndFavourite(userId, true);
        List<Integer> restaurantIds = favReviews.stream()
                .map(Review::getRestaurantID)
                .collect(Collectors.toList());
        return restaurantRepository.findAllById(restaurantIds);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            // Validate user data
            if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
                response.put("message", "All fields are required.");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if the username already exists
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                response.put("message", "Username already exists.");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if the email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                response.put("message", "Email already exists.");
                return ResponseEntity.badRequest().body(response);
            }

            // Save the user
            userService.signUp(user);
            response.put("message", "User  signed up successfully!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "An error occurred during signup.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        Optional<User> existingUser  = userRepository.findByUsername(user.getUsername());

        if (!existingUser .isPresent()) {
            response.put("message", "Username not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!existingUser .get().getPassword().equals(user.getPassword())) {
            response.put("message", "Incorrect password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Login successful");
        response.put("userId", existingUser .get().getId()); // Include user ID in response
        return ResponseEntity.ok(response);
    }
}