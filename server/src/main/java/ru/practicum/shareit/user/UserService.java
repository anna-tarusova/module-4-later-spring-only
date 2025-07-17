package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    User saveUser(User user);
    Optional<User> getUser(long id);
    User createUser(User user);
    void deleteUser(long id);
}