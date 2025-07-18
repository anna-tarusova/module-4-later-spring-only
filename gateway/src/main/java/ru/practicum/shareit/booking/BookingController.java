package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemClient;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static ru.practicum.shareit.Constants.USER_CUSTOM_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_CUSTOM_HEADER) long bookerId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("create, bookerId = {}, bookingDto = {}", bookerId, bookingDto.toString());
        Map<String, String> errorResponse = new HashMap<>();
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            log.error("create, start = end = {}", bookingDto.getStart());
            errorResponse.put("error", "Дата начала равна дате конца");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (bookingDto.getStart().isBefore(Instant.now())) {
            log.error("create, start in the past");
            errorResponse.put("error", "Дата начала в прошлом");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (bookingDto.getEnd().isBefore(Instant.now())) {
            log.error("create, end in the past");
            errorResponse.put("error", "Дата конца в прошлом");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        return bookingClient.create(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader(USER_CUSTOM_HEADER) long ownerId,
                                              @PathVariable("bookingId") long bookingId,
                                              @RequestParam("approved") boolean approved) {
        log.info("approve, ownerId = {}, bookingId = {}, approved = {}", ownerId, bookingId, approved);
        return bookingClient.approve(ownerId, bookingId, approved);
    }
}
