package ru.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exceptions.ForbiddenException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import java.util.List;
import java.util.Optional;

import static ru.practicum.Constants.USER_CUSTOM_HEADER;
import static ru.practicum.item.ItemMapper.toDto;
import static ru.practicum.item.ItemMapper.toEntity;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader(USER_CUSTOM_HEADER) long userId) {
        log.info("getItemsOfUser, userId = {}", userId);
        return itemService.getItems(userId).stream().map(ItemMapper::toDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_CUSTOM_HEADER) long userId, @RequestParam("text") String text) {
        log.info("search, userId = {}, text = {}", userId, text);
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemService.getItems(userId, text).stream().map(ItemMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(USER_CUSTOM_HEADER) Long userId,
                                           @PathVariable("id") Long itemId) {
        log.info("getItem, userId = {}, itemId = {}", userId, itemId);
        Optional<Item> item = itemService.getItem(userId, itemId);
        return item.map(value -> new ResponseEntity<>(toDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader(USER_CUSTOM_HEADER) Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {

        log.info("add, userId = {}, itemDto = {}", userId, itemDto.toString());
        Optional<User> user = userService.getUser(userId);
        if (user.isEmpty()) {
            log.error("add, user not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Item item = toEntity(itemDto);
        item.setUserId(userId);
        item.setRequestId(itemDto.getRequestId());
        return new ResponseEntity<>(toDto(itemService.addNewItem(item)), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_CUSTOM_HEADER) Long userId,
                                          @PathVariable("id") Long itemId,
                                          @RequestBody ItemDto itemDto) {
        log.info("update, userId = {}, itemDto = {}", userId, itemDto.toString());
        Optional<User> user = userService.getUser(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Item> itemOpt = itemService.getItem(userId, itemId);
        if (itemOpt.isEmpty()) {
            log.error("update, item is not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Item item = itemOpt.get();

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getName() != null) {
            if (itemDto.getName().isEmpty()) {
                return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
            }
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isEmpty()) {
                return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
            }
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }

        item.setUserId(userId);
        return new ResponseEntity<>(toDto(itemService.updateItem(item)), HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@RequestHeader(USER_CUSTOM_HEADER) long userId,
                           @PathVariable(name = "itemId") long itemId) {

        try {
            log.info("delete, userId = {}, itemId = {}", userId, itemId);
            itemService.deleteItem(userId, itemId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error("delete, error = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> comment(@RequestHeader(USER_CUSTOM_HEADER) long userId,
                                        @PathVariable(name = "itemId") long itemId,
                                        @Valid @RequestBody CommentDto commentDto) {

        try {
            log.info("comment, userId = {}, itemId = {}, comment = {}", userId, itemId, commentDto.toString());
            Comment comment = CommentMapper.toEntity(commentDto);
            comment.setItemId(itemId);
            User author = new User();
            author.setId(userId);
            comment.setAuthor(author);
            comment = commentService.addComment(comment);
            return new ResponseEntity<>(CommentMapper.toDto(comment), HttpStatus.OK);
        } catch (ForbiddenException e) {
            log.error("comment, forbidden = {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}