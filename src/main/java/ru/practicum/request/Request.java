package ru.practicum.request;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.item.Item;

import java.time.Instant;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, name = "user_id")
    Long userId;

    @Column(nullable = false, length = 4000)
    String description;

    @Column(nullable = false)
    Instant created;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    List<Item> items;

    public Request(String description) {
        setDescription(description);
    }
}
