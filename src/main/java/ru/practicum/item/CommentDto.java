package ru.practicum.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.deserializers.InstantDeserializer;
import ru.practicum.serializers.InstantSerializer;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    Long itemId;
    Long bookerId;
    String authorName;

    @NotNull(message = "Поле comment не может быть пустым")
    @JsonProperty("text")
    @Size(min = 10, max = 4000)
    String comment;

    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    Instant created;

    public CommentDto(Long id, Long itemId, String authorName, String comment, Instant created) {
        setId(id);
        setItemId(itemId);
        setAuthorName(authorName);
        setComment(comment);
        setCreated(created);
    }
}
