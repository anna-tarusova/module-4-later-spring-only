package ru.practicum.item;

import java.util.List;
import java.util.Optional;

interface ItemRepository {

    List<Item> findByUserId(long userId);

    List<Item> findByUserId(long userId, String text);

    Optional<Item> get(long userId, long itemId);

    Item add(Item item);

    Item update(Item item);

    void deleteByUserIdAndItemId(long userId, long itemId);
}