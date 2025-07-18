package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;
    private final String EMAIL_REGEX = "(?i)^\\w+(\\.\\w+)*@\\w+(\\.\\w+)*$";

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("get users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") long id) {
        log.info("get user, id = {}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDto user) {
        log.info("create user");
        return userClient.createUser(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") long id, @RequestBody UserDto user) {
        log.info("update user");
        if (user.getEmail() != null) {
            if (user.getEmail().isEmpty() || !user.getEmail().matches(EMAIL_REGEX)) {
                return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
            }
        }
        return userClient.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") long id) {
        log.info("delete user");
        return userClient.deleteUser(id);
    }
}