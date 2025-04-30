// File: src/main/java/org/example/Mappers/UserMapper.java
package org.example.Mappers;

import org.example.DTOs.UserDTO;
import org.example.Models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}