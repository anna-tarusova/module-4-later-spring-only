package ru.practicum.booking;

import ru.practicum.item.Item;
import ru.practicum.item.ItemMapper;
import ru.practicum.user.UserMapper;

public class BookingMapper {
    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(booking.getId(), booking.getItem().getId(),
                ItemMapper.toDto(booking.getItem()),
                UserMapper.toDto(booking.getBooker()),
                booking.getStart(), booking.getEnd(), booking.getState());
    }

    public static Booking toEntity(BookingDto bookingDto) {
        Booking booking = new Booking();
        Item item = new Item();
        item.setId(bookingDto.getItemId());
        booking.setItem(item);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }
}
