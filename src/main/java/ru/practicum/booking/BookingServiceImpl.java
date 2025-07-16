package ru.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.ItemRepository;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking addNewBooking(Booking booking) {
        Optional<User> booker = userRepository.findById(booking.getBooker().getId());
        if (booker.isEmpty()) {
            throw new ForbiddenException("Несуществующий пользователь");
        }

        Optional<Item> item = itemRepository.findById(booking.getItem().getId());
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет не существует");
        }

        if (!item.get().getAvailable()) {
            throw new BadRequestException("Предмет недоступен");
        }

        booking = repository.save(booking);
        booking.setBooker(booker.get());
        booking.setItem(item.get());
        return booking;
    }

    @Override
    public Booking approve(long ownerId, long bookingId, boolean approve) {
        Optional<Booking> bookingOpt = repository.findById(bookingId);

        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("Такого бронирования не существует");
        }

        Booking booking = bookingOpt.get();
        if (!booking.getItem().getUserId().equals(ownerId)) {
            throw new ForbiddenException("Предмет принадлежит другому пользователю");
        }

        booking.setState(approve ? State.APPROVED : State.REJECTED);
        return repository.save(booking);
    }

    @Override
    public Booking getById(long id) {
        Optional<Booking> booking = repository.findById(id);
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
        return booking.get();
    }

    @Override
    public List<Booking> getBookingsOfBooker(long bookerId, State state) {
        if (state == null) {
            return repository.findByBookerId(bookerId);
        }
        return repository.findByBookerIdAndState(bookerId, state);
    }

    @Override
    public List<Booking> getBookingsOfOwner(long ownerId, State state) {
        if (state == null) {
            return repository.findByOwnerId(ownerId);
        }
        return repository.findByOwnerIdAndState(ownerId, state);
    }
}
