package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Map<String, String> addErrorMessage(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        log.error(ex.getMessage());
        return errorResponse;
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequestException(Exception ex) {
        Map<String, String> errorResponse = addErrorMessage(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(Exception ex) {
        Map<String, String> errorResponse = addErrorMessage(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(Exception ex) {
        Map<String, String> errorResponse = addErrorMessage(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}