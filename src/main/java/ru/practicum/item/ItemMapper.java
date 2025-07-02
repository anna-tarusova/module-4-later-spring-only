package ru.practicum.item;

import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        return new ItemDto(item.getId(), item.getUserId(), item.getUrl(), item.getAvailable(), item.getDescription(), item.getName());
    }

    public static Item toEntity(ItemDto itemDto) {
        if (itemDto == null) return null;
        return new Item(itemDto.getId(), itemDto.getUserId(), itemDto.getUrl(), itemDto.getAvailable(), itemDto.getDescription(), itemDto.getName());
    }
}