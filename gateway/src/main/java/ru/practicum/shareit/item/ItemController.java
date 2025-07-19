package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.Constants.USER_CUSTOM_HEADER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_CUSTOM_HEADER) long userId) {
        log.info("get items");
        return itemClient.getItemsOfUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_CUSTOM_HEADER) long userId, @RequestParam("text") String text) {
        log.info("search");
        return itemClient.search(userId, text);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_CUSTOM_HEADER) long userId, @PathVariable("id") long itemId) {
        log.info("get item");
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping()
    public ResponseEntity<Object> addItem(@RequestHeader(USER_CUSTOM_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("add item");
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader(USER_CUSTOM_HEADER) Long userId,
                                          @PathVariable("id") Long itemId,
                                          @RequestBody ItemDto itemDto) {
        if (itemDto.getName() != null) {
            if (itemDto.getName().isEmpty()) {
                return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
            }
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isEmpty()) {
                return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
            }
        }
        itemDto.setId(itemId);
        return itemClient.updateItem(userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(USER_CUSTOM_HEADER) long userId,
                                              @PathVariable(name = "itemId") long itemId) {

        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@RequestHeader(USER_CUSTOM_HEADER) long userId,
                                              @PathVariable(name = "itemId") long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
