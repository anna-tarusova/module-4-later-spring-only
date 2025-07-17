package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Comment addComment(Comment comment) {
        Optional<Booking> lastBookingOpt = bookingRepository.getLastBooking(comment.getItemId(), comment.getAuthor().getId());
        if (lastBookingOpt.isEmpty()) {
            throw new ForbiddenException("Пользователь не букал этот предмет");
        }

        Booking lastBooking = lastBookingOpt.get();
        if (!lastBooking.getState().equals(State.APPROVED)) {
            throw new ForbiddenException("Пользователю не заапрувили бронирование");
        }

        if (!lastBooking.getEnd().isBefore(Instant.now())) {
            throw new BadRequestException("Бронирование ещё не закончено");
        }

        comment.setCreated(Instant.now());
        comment = repository.save(comment);
        User author = userRepository.findById(comment.getAuthor().getId()).orElseThrow();
        comment.setAuthor(author);
        return comment;
    }
}
