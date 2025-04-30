// File: src/main/java/org/example/Factories/UserFactory.java
package org.example.Factories;

import org.example.Models.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        return user;
    }
}