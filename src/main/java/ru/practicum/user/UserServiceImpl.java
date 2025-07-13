package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public Optional<User> getUser(long id) {
        return repository.getUser(id);
    }

    @Override
    public User createUser(User user) {
        return repository.createUser(user);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteUser(id);
    }
}