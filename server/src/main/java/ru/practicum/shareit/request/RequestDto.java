package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.deserializers.InstantDeserializer;
import ru.practicum.shareit.item.ItemShortDto;
import ru.practicum.shareit.serializers.InstantSerializer;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    Long Id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 4000)
    String description;

    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    Instant created;

    List<ItemShortDto> items;

    public RequestDto(long id, String description, Instant created) {
        setId(id);
        setDescription(description);
        setCreated(created);
    }
}
