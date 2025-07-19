package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;

    @Override
    public Request addRequest(Request request) {
        ensureUserExists(request.getUserId());
        request.setCreated(Instant.now());
        return repository.save(request);
    }

    @Override
    public Request get(long userId, long requestId) {
        ensureUserExists(userId);
        Optional<Request> requestOpt = repository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new NotFoundException(String.format("Реквест с id = %d не существует", requestId));
        }
        return requestOpt.get();
    }

    @Override
    public List<Request> getMyRequests(long userId) {
        ensureUserExists(userId);
        return repository.findByUserIdOrderByCreatedDesc(userId);
    }

    @Override
    public List<Request> getOtherRequests(long userId) {
        ensureUserExists(userId);
        return repository.findByUserIdNotOrderByCreatedDesc(userId);
    }

    private void ensureUserExists(long userId)
    {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", userId));
        }
    }
}
