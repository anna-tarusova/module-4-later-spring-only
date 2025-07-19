package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

import static ru.practicum.shareit.Constants.USER_CUSTOM_HEADER;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping()
    public ResponseEntity<RequestDto> create(@RequestHeader(USER_CUSTOM_HEADER) long userId,
                                              @Valid @RequestBody RequestDto requestDto) {

        try {
            log.info("create, userId = {}, description = {}", userId, requestDto.getDescription());
            Request request = RequestMapper.toEntity(requestDto);
            request.setUserId(userId);
            request = requestService.addRequest(request);
            return new ResponseEntity<>(RequestMapper.toDto(request), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("create, not found = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> get(@RequestHeader(USER_CUSTOM_HEADER) long userId,
                                             @Valid @PathVariable("requestId") long requestId) {

        try {
            log.info("get, userId = {}, requestId = {}", userId, requestId);
            Request request = requestService.get(userId, requestId);
            return new ResponseEntity<>(RequestMapper.toDto(request), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("get, not found = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping()
    public ResponseEntity<List<RequestDto>> getMyRequests(@RequestHeader(USER_CUSTOM_HEADER) long userId) {

        try {
            log.info("getMyRequests, userId = {}", userId);
            List<Request> requests = requestService.getMyRequests(userId);
            return new ResponseEntity<>(requests.stream().map(RequestMapper::toDto).toList(), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("getMyRequests, user not found = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getOtherRequests(@RequestHeader(USER_CUSTOM_HEADER) long userId) {

        try {
            log.info("getOtherRequests, userId = {}", userId);
            List<Request> requests = requestService.getOtherRequests(userId);
            return new ResponseEntity<>(requests.stream().map(RequestMapper::toDto).toList(), HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("getOtherRequests, user not found = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
