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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
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
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        User newUser = UserFactory.create(nextId, user.getUsername(), hashedPassword, user.getEmail());
        return userRepository.save(newUser);
    }

    private int getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().inc("seq", 1);
        Counter counter = mongoTemplate.findAndModify(query, update, org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true).upsert(true), Counter.class);
        return counter.getSeq();
    }

    public String login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "Username does not exist.";
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Incorrect password.";
        }

        return "Login successful";
    }

}