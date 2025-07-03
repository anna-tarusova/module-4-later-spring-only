package ru.practicum.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.ConflictException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static ru.practicum.user.UserMapper.toDto;
import static ru.practicum.user.UserMapper.toEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final String emailRegex = "(?i)^\\w+(\\.\\w+)*@\\w+(\\.\\w+)*$";
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream().map(UserMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") long id) {
        Optional<User> user = userService.getUser(id);
        return user.map(value -> new ResponseEntity<>(toDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> saveNewUser(@PathVariable("id") long id, @RequestBody UserDto user) {
        try {
            Optional<User> currentUserOpt = userService.getUser(id);
            User currentUser = currentUserOpt.orElseThrow();
            if (user.getEmail() != null) {
                if (user.getEmail().isEmpty() || !user.getEmail().matches(emailRegex)) {
                    return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
                }
                currentUser.setEmail(user.getEmail());
            }
            currentUser.setName(user.getName());
            return new ResponseEntity<>(toDto(userService.saveUser(currentUser)), HttpStatus.OK);
        } catch (ConflictException e) {
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createNewUser(@Valid @RequestBody UserDto user) {
        try {
            return new ResponseEntity<>(toDto(userService.createUser(toEntity(user))), HttpStatus.CREATED);
        } catch (ConflictException e) {
            return new ResponseEntity<>(user, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteNewUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
    }
}