package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    Long userId;

    @Size(max = 1000)
    String url;

    @NotNull(message = "available должен быть задан")
    Boolean available;

    @NotBlank(message = "Описание продукта не может быть пустым")
    @Size(min = 10, max = 4000)
    String description;

    @NotBlank(message = "Название продукта не может быть пустым")
    @Size(min = 1, max = 255)
    String name;

    @Nullable
    Long requestId;
}