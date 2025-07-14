package ru.practicum.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long bookerId);
    List<Booking> findByBookerIdAndState(long bookerId, State state);

    @Query(value = "SELECT * FROM Bookings b WHERE b.item_id = ?1 and b.endDate < ?2", nativeQuery = true)
    Optional<Booking> findByItemIdBefore(long itemId, Instant date);

    @Query(value =  "SELECT * FROM Bookings b WHERE b.item_id = ?1 and b.start > ?2", nativeQuery = true)
    Optional<Booking> findByItemIdAfter(long itemId, Instant date);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.item.userId = ?1")
    List<Booking> findByOwnerId(long ownerId);
    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.item.userId = ?1 AND b.state = ?2")
    List<Booking> findByOwnerIdAndState(long ownerId, State state);

    @Query(value = "SELECT * from Bookings b WHERE b.item_id = ?1 and b.booker_id = ?2 ORDER BY b.Id desc LIMIT 1", nativeQuery = true)
    Optional<Booking> getLastBooking(long itemId, long bookerId);
}
