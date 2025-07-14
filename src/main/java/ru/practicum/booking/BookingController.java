package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static ru.practicum.booking.BookingMapper.toDto;
import static ru.practicum.booking.BookingMapper.toEntity;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String userCustomHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader(userCustomHeader) long bookerId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (bookingDto.getStart().isBefore(Instant.now())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (bookingDto.getEnd().isBefore(Instant.now())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Booking booking = toEntity(bookingDto);
        User booker = new User();
        booker.setId(bookerId);
        booking.setBooker(booker);

        try {
            booking = bookingService.addNewBooking(booking);
            return new ResponseEntity<>(toDto(booking), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@RequestHeader(userCustomHeader) long ownerId,
                                              @PathVariable("bookingId") long bookingId,
                                              @RequestParam("approved") boolean approved) {
        try {
            Booking booking = bookingService.approve(ownerId, bookingId, approved);
            return new ResponseEntity<>(toDto(booking), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ForbiddenException e) {
            //так того требует 1 тест, возвращать Booking
            Booking booking = bookingService.getById(bookingId);
            return new ResponseEntity<>(toDto(booking), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader(userCustomHeader) long ownerOrBookerId,
                                              @PathVariable("bookingId") long bookingId) {
        try {
            Booking booking = bookingService.getById(bookingId);
            if (!booking.getItem().getUserId().equals(ownerOrBookerId) &&
                    !booking.getBooker().getId().equals(ownerOrBookerId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(toDto(booking), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getAllBookings(@RequestHeader(userCustomHeader) long bookerId,
                                                           @RequestParam(value = "state", required = false) State state) {
        List<Booking> bookings = bookingService.getBookingsOfBooker(bookerId, state);
        return new ResponseEntity<>(bookings.stream().map(BookingMapper::toDto).toList(), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(@RequestHeader(userCustomHeader) long ownerId,
                                                                  @RequestParam(value = "state", required = false) State state) {
        Optional<User> user = userService.getUser(ownerId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Booking> bookings = bookingService.getBookingsOfOwner(ownerId, state);
        return new ResponseEntity<>(bookings.stream().map(BookingMapper::toDto).toList(), HttpStatus.OK);
    }
}
