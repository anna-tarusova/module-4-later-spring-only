package ru.practicum.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static ru.practicum.Constants.USER_CUSTOM_HEADER;
import static ru.practicum.booking.BookingMapper.toDto;
import static ru.practicum.booking.BookingMapper.toEntity;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader(USER_CUSTOM_HEADER) long bookerId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("create, bookerId = {}, bookingDto = {}", bookerId, bookingDto.toString());
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.error("create, start = end = {}", bookingDto.getStart());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (bookingDto.getStart().isBefore(Instant.now())) {
            log.error("create, start in the past");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (bookingDto.getEnd().isBefore(Instant.now())) {
            log.error("create, end in the past");
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
            log.error("create, not found, error = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            log.error("create, bad request exception, error = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ForbiddenException e) {
            log.error("create, forbidden exception, error = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@RequestHeader(USER_CUSTOM_HEADER) long ownerId,
                                              @PathVariable("bookingId") long bookingId,
                                              @RequestParam("approved") boolean approved) {
        try {
            log.info("approve, ownerId = {}, bookingId = {}, approved = {}", ownerId, bookingId, approved);
            Booking booking = bookingService.approve(ownerId, bookingId, approved);
            return new ResponseEntity<>(toDto(booking), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("approve, not found exception, error = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ForbiddenException e) {
            log.error("approve, forbidden exception, error = {}", e.getMessage());
            //так того требует 1 тест, возвращать Booking
            Booking booking = bookingService.getById(bookingId);
            return new ResponseEntity<>(toDto(booking), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader(USER_CUSTOM_HEADER) long ownerOrBookerId,
                                              @PathVariable("bookingId") long bookingId) {
        try {
            log.info("getBooking, ownerOrBookerId = {}, bookingId = {}", ownerOrBookerId, bookingId);
            Booking booking = bookingService.getById(bookingId);
            if (!booking.getItem().getUserId().equals(ownerOrBookerId) &&
                    !booking.getBooker().getId().equals(ownerOrBookerId)) {
                log.error("getBooking, forbidden, item.userId != ownerOrBookerId");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(toDto(booking), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("getBooking, not found, e = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<List<BookingDto>> getAllBookings(@RequestHeader(USER_CUSTOM_HEADER) long bookerId,
                                                           @RequestParam(value = "state", required = false) State state) {
        log.info("getAllBookings, bookerId = {}, state = {}", bookerId, state);
        List<Booking> bookings = bookingService.getBookingsOfBooker(bookerId, state);
        return new ResponseEntity<>(bookings.stream().map(BookingMapper::toDto).toList(), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(@RequestHeader(USER_CUSTOM_HEADER) long ownerId,
                                                                  @RequestParam(value = "state", required = false) State state) {
        log.info("getAllBookingsByOwner, ownerId = {}, state = {}", ownerId, state);
        Optional<User> user = userService.getUser(ownerId);
        if (user.isEmpty()) {
            log.error("getAllBookingsByOwner, user is not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Booking> bookings = bookingService.getBookingsOfOwner(ownerId, state);
        return new ResponseEntity<>(bookings.stream().map(BookingMapper::toDto).toList(), HttpStatus.OK);
    }
}
