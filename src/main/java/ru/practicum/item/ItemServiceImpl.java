package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return repository.findByUserId(userId, text);
    }

    @Override
    public Optional<Item> getItem(long userId, long itemId) {
        return repository.get(userId, itemId);
    }

    @Override
    public Item addNewItem(Item item) {
        return repository.add(item);
    }

    @Override
    public Item updateItem(Item item) {
        return repository.update(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        deleteItem(userId, itemId);
    }
}
