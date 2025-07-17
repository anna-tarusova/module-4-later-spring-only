package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> getItems(long userId);
    List<Item> getItems(long userId, String text);
    Optional<Item> getItem(long userId, long itemId);
    Item addNewItem(Item item);
    Item updateItem(Item item);
    void deleteItem(long userId, long itemId);
}
