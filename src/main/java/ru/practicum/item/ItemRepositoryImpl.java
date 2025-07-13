package ru.practicum.item;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final List<Item> items = new ArrayList<>();

    @Override
    public List<Item> findByUserId(long userId) {
        return items.stream().filter(i -> i.getUserId() == userId).toList();
    }

    @Override
    public List<Item> findByUserId(long userId, String text) {
        String finalText = text.toLowerCase();
        return items.stream().filter(i -> i.getUserId() == userId && i.getName().toLowerCase().contains(finalText)).toList();
    }

    @Override
    public Optional<Item> get(long userId, long itemId) {
        return items.stream().filter(i -> i.getUserId() == userId && i.getId() == itemId).findFirst();
    }

    @Override
    public Item add(Item item) {
        item.setId(getId());
        items.add(item);
        return item;
    }

    @Override
    public Item update(Item item) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).getId(), item.getId())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            items.remove(index);
            items.add(item);
        }
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getId() == itemId && item.getUserId() == userId) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            items.remove(index);
        }
    }

    private long getId() {
        long lastId = items.stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
