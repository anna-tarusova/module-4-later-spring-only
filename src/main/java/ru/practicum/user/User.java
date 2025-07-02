package ru.practicum.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class User {
    private Long id;
    private String email;
    private String name;

    public User(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}