package ru.practicum.user;

import java.util.List;
import java.util.Optional;

interface UserRepository {
    List<User> findAll();
    User save(User user);
    Optional<User> getUser(long id);
    User createUser(User user);
    void deleteUser(long id);
}