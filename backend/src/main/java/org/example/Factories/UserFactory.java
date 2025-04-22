package org.example.Factories;

import org.example.Models.User;

public class UserFactory {

    public static User create(Integer id, String username, String password, String email) {
        return new User(id, username, password, email);
    }
}