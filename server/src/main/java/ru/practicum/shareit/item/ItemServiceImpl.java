package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;

    @Override
    public List<Item> getItems(long userId) {
        List<Item> items = repository.findByUserId(userId);
        items.forEach(item -> {
            Optional<Booking> lastBooking = bookingRepository.findByItemIdBefore(item.getId(), Instant.now());
            Optional<Booking> nextBooking = bookingRepository.findByItemIdAfter(item.getId(), Instant.now());
            lastBooking.ifPresent(item::setLastBooking);
            nextBooking.ifPresent(item::setNextBooking);
        });
        return items;
    }

    @Override
    public List<Item> getItems(long userId, String text) {
        return repository.search(userId, text);
    }

    @Override
    public Optional<Item> getItem(long userId, long itemId) {
        return repository.findById(itemId);
    }

    @Override
    public Item addNewItem(Item item) {
        item.setId(null);
        return repository.save(item);
    }

    @Override
    public Item updateItem(Item item) {
        return repository.save(item);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        Optional<Item> item = repository.findById(itemId);
        if (item.isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }

        if (item.get().getUserId() != userId) {
            throw new ForbiddenException("Предмет не принадлежит пользователю");
        }
        repository.deleteById(itemId);
    }
}
