package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static Comment toEntity(CommentDto dto) {
        return new Comment(dto.getId(), dto.getItemId(), dto.getComment(), dto.getCreated());
    }

    public static CommentDto toDto(Comment entity) {
        return new CommentDto(entity.getId(), entity.getItemId(), entity.getAuthor().getName(), entity.getComment(), entity.getCreated());
    }
}
