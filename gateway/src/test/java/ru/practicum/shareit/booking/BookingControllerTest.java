package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingClient bookingClient;

    private static final String USER_HEADER = Constants.USER_CUSTOM_HEADER;

    private BookingDto validBookingDto;
    private BookingDto sameStartEndDto;
    private BookingDto startInPastDto;
    private BookingDto endInPastDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Валидный DTO
        validBookingDto = new BookingDto();
        validBookingDto.setStart(Instant.now().plusSeconds(3600));
        validBookingDto.setEnd(Instant.now().plusSeconds(7200));

        // start == end
        sameStartEndDto = new BookingDto();
        Instant now = Instant.now().plusSeconds(3600);
        sameStartEndDto.setStart(now);
        sameStartEndDto.setEnd(now);

        // start в прошлом
        startInPastDto = new BookingDto();
        startInPastDto.setStart(Instant.now().minusSeconds(3600));
        startInPastDto.setEnd(Instant.now().plusSeconds(3600));

        // end в прошлом
        endInPastDto = new BookingDto();
        endInPastDto.setStart(Instant.now().plusSeconds(3600));
        endInPastDto.setEnd(Instant.now().minusSeconds(3600));
    }

    @Test
    void testCreate_ValidBooking_CallsClient() {
        long bookerId = 1L;

        when(bookingClient.create(bookerId, validBookingDto))
                .thenReturn(ResponseEntity.ok("Success"));

        ResponseEntity<Object> response = bookingController.create(bookerId, validBookingDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(bookingClient, times(1)).create(bookerId, validBookingDto);
    }

    @Test
    void testCreate_StartEqualsEnd_ReturnsBadRequest() {
        long bookerId = 1L;

        ResponseEntity<Object> response = bookingController.create(bookerId, sameStartEndDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Дата начала равна дате конца"));
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void testCreate_StartInPast_ReturnsBadRequest() {
        long bookerId = 1L;

        ResponseEntity<Object> response = bookingController.create(bookerId, startInPastDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Дата начала в прошлом"));
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void testCreate_EndInPast_ReturnsBadRequest() {
        long bookerId = 1L;

        ResponseEntity<Object> response = bookingController.create(bookerId, endInPastDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Дата конца в прошлом"));
        verify(bookingClient, never()).create(anyLong(), any());
    }

    @Test
    void testApprove_CallsClientCorrectly() {
        long ownerId = 1L;
        long bookingId = 100L;
        boolean approved = true;

        ResponseEntity<Object> mockResponse = ResponseEntity.ok("Approved");
        when(bookingClient.approve(ownerId, bookingId, approved)).thenReturn(mockResponse);

        ResponseEntity<Object> response = bookingController.approve(ownerId, bookingId, approved);

        assertEquals(mockResponse, response);
        verify(bookingClient, times(1)).approve(ownerId, bookingId, approved);
    }
}
