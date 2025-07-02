package ru.practicum.item;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private Long userId;
    private String url;
    private Boolean available;
    private String description;
    private String name;

    public Item(Long id, Long userId, String url, Boolean available, String description, String name) {
        this.id = id;
        this.userId = userId;
        this.url = url;
        this.available = available;
        this.description = description;
        this.name = name;
    }

    public Item() {
    }
}