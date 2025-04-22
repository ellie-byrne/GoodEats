package org.example.Mappers;

import org.example.Models.User;
import org.example.DTOs.UserDTO;

public class UserMapper {

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) return null;

        return new User(dto.getId(), dto.getUsername(), null, dto.getEmail());
    }
}