package ru.practicum.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) return null;
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }
}