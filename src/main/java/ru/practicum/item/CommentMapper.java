package ru.practicum.item;

import ru.practicum.user.User;

public class CommentMapper {
    public static Comment toEntity(CommentDto dto) {
        User author = new User();
        author.setId(dto.getBookerId());
        return new Comment(dto.getId(), dto.getItemId(), author, dto.getComment(), dto.getCreated());
    }

    public static CommentDto toDto(Comment entity) {
        return new CommentDto(entity.getId(), entity.getItemId(), entity.getAuthor().getName(), entity.getComment(), entity.getCreated());
    }
}
