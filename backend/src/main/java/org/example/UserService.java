package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Document(collection = "counters")
@Data
class Counter {
    @Id
    private String id;
    private int seq;
}

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public String signUp(User user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "All fields are required!";
        }

        Optional<User> existingUserByUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserByUsername.isPresent()) {
            return "Username is already taken.";
        }

        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            return "Email is already taken.";
        }

        try {
            int nextId = getNextSequence("userId");
            user.setId(nextId);
            userRepository.save(user);
            return "User signed up successfully!";
        } catch (Exception e) {
            System.err.println("Error during signup: " + e.getMessage());
            e.printStackTrace();
            return "Database error during signup: " + e.getMessage();
        }
    }

    private int getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        Counter counter = mongoTemplate.findAndModify(query, update, org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true).upsert(true), Counter.class);
        return counter.getSeq();
    }

    public String login(String username, String password) {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "Username does not exist.";
        }

        User user = userOptional.get();

        // Check if password matches
        if (!user.getPassword().equals(password)) {
            return "Incorrect password.";
        }

        return "Login successful!";
    }
}