package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.ConflictException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static ru.practicum.user.UserMapper.toDto;
import static ru.practicum.user.UserMapper.toEntity;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final String EMAIL_REGEX = "(?i)^\\w+(\\.\\w+)*@\\w+(\\.\\w+)*$";
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("getAllUsers");
        List<User> users = userService.getAllUsers();
        return users.stream().map(UserMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") long id) {
        log.info("getUser, id = {}", id);
        Optional<User> user = userService.getUser(id);
        return user.map(value -> new ResponseEntity<>(toDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> saveNewUser(@PathVariable("id") long id, @RequestBody UserDto user) {
        try {
            log.info("getUser, id = {}, user = {}", id, user.toString());
            Optional<User> currentUserOpt = userService.getUser(id);
            User currentUser = currentUserOpt.orElseThrow();
            if (user.getEmail() != null) {
                if (user.getEmail().isEmpty() || !user.getEmail().matches(EMAIL_REGEX)) {
                    return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
                }
                currentUser.setEmail(user.getEmail());
            }
            if (user.getName() != null && !user.getName().isBlank()) {
                currentUser.setName(user.getName());
            }
            return new ResponseEntity<>(toDto(userService.saveUser(currentUser)), HttpStatus.OK);
        } catch (ConflictException e) {
            log.error("getUser, conflict, error = {}", e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        } catch (NoSuchElementException e) {
            log.error("getUser, not found, error = {}", e.getMessage());
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createNewUser(@Valid @RequestBody UserDto user) {
        try {
            log.info("createNewUser, user = {}", user.toString());
            return new ResponseEntity<>(toDto(userService.createUser(toEntity(user))), HttpStatus.CREATED);
        } catch (ConflictException e) {
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        log.info("deleteUser, id = {}", id);
        userService.deleteUser(id);
    }
}