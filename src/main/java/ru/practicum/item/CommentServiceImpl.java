package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingRepository;
import ru.practicum.booking.State;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ForbiddenException;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final BookingRepository bookingRepository;

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
        comment = repository.findById(comment.getId()).orElseThrow();
        return comment;
    }
}
