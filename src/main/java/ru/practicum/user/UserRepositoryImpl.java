package ru.practicum.user;

import org.springframework.stereotype.Component;
import ru.practicum.exceptions.ConflictException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public User save(User user) {
        if (users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()) && !Objects.equals(u.getId(), user.getId()))) {
            throw new ConflictException("Пользователь с таким email уже есть");
        }

        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            if (Objects.equals(users.get(i).getId(), user.getId())) {
                index = i;
            }
        }

        if (index != -1) {
            users.remove(index);
            users.add(user);
        }

        return user;
    }

    @Override
    public Optional<User> getUser(long id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    @Override
    public User createUser(User user) {
        if (users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с таким email уже есть");
        }
        user.setId(getId());
        users.add(user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        int index = -1;
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getId() == id) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            users.remove(index);
        }
    }

    private long getId() {
        long lastId = users.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
