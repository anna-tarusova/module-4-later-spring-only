package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    Item item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booker_id")
    User booker;

    Instant start;
    @Column(name = "end_date")
    Instant end;
    @Enumerated(EnumType.STRING)
    State state;

    public Booking() {
        state = State.WAITING;
    }
}
