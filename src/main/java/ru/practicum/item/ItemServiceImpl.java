package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public List<Item> getItems(long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<Item> getItems(long userId, String text) {
        return repository.search(userId, text);
    }

    @Override
    public Optional<Item> getItem(long userId, long itemId) {
        Optional<Item> item = repository.findById(itemId);

        if (item.isPresent() && !item.get().getUserId().equals(userId)) {
            throw new ForbiddenException("Предмет не принадлежит пользователю");
        }

        return item;
    }

    @Override
    public Item addNewItem(Item item) {
        item.setId(null);
        return repository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        return repository.save(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Optional<Item> item = repository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }

        if (item.get().getUserId() != userId) {
            throw new ForbiddenException("Предмет не принадлежит пользователю");
        }
        repository.deleteById(itemId);
    }
}
