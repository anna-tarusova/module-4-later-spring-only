package ru.practicum.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.booking.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    Long userId;
    String url;
    @NotNull(message = "available должен быть задан")
    Boolean available;
    @NotBlank(message = "Описание продукта не может быть пустым")
    String description;
    @NotBlank(message = "Название продукта не может быть пустым")
    String name;

    List<CommentDto> comments;

    BookingDto lastBooking;
    BookingDto nextBooking;

    public ItemDto(Long id, Long userId, String url, Boolean available, String description, String name) {
        setId(id);
        setUserId(userId);
        setUrl(url);
        setAvailable(available);
        setDescription(description);
        setName(name);
    }
}