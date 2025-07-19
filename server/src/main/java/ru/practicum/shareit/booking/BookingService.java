package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking addNewBooking(Booking item);
    Booking approve(long ownerId, long bookingId, boolean approve);
    Booking getById(long id);
    List<Booking> getBookingsOfBooker(long bookerId, State state);
    List<Booking> getBookingsOfOwner(long ownerId, State state);

}
