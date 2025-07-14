package ru.practicum.item;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.booking.Booking;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_id", nullable = false)
    Long userId;
    @Column(length = 1000)
    String url;
    @Column(nullable = false)
    Boolean available;
    @Column(nullable = false)
    String description;
    @Column(nullable = false)
    String name;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    List<Comment> comments;

    @Transient
    Booking lastBooking;

    @Transient
    Booking nextBooking;

    public Item(Long id, Long userId, String url, boolean available, String description, String name) {
        setId(id);
        setUserId(userId);
        setUrl(url);
        setAvailable(available);
        setDescription(description);
        setName(name);
    }
}