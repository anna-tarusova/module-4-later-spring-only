package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.deserializers.InstantDeserializer;
import ru.practicum.shareit.serializers.InstantSerializer;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    @NotNull(message = "Поле itemId не может быть пустым")
    Long itemId;
    @NotNull(message = "Поле start не может быть пустым")
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    Instant start;
    @NotNull(message = "Поле end не может быть пустым")
    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    Instant end;
    @JsonProperty("status")
    State state;
}
