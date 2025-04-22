package org.example.Services;

import lombok.Data;
import org.example.Models.User;
import org.example.Respositories.UserRepository;
import org.example.Factories.UserFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Document(collection = "counters")
@Data
class Counter {
    @Id
    private String id;
    private int seq;

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }
    }

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public UserService(UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public User signUp(User user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            throw new IllegalArgumentException("All fields are required!");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already taken.");
        }

        int nextId = getNextSequence("userId");
        User newUser = UserFactory.create(nextId, user.getUsername(), user.getPassword(), user.getEmail());
        return userRepository.save(newUser);
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