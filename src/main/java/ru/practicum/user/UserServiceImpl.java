package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.ConflictException;

import java.util.List;
import java.util.Objects;
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
        Optional<User> existingUser = repository.findUserByEmail(user.getEmail());
        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), user.getId())) {
            throw new ConflictException("Пользователь с таким email уже есть");
        }
        return repository.save(user);
    }

    @Override
    public Optional<User> getUser(long id) {

        return repository.findById(id);
    }

    @Override
    public User createUser(User user) {
        user.setId(null);
        Optional<User> existingUser = repository.findUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new ConflictException("Пользователь с таким email существует");
        }
        return repository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}