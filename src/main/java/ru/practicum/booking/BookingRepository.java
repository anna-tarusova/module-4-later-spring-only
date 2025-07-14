package ru.practicum.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long bookerId);
    List<Booking> findByBookerIdAndState(long bookerId, State state);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.item.userId = ?1")
    List<Booking> findByOwnerId(long ownerId);
    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.item.userId = ?1 AND b.state = ?2")
    List<Booking> findByOwnerIdAndState(long ownerId, State state);
}
