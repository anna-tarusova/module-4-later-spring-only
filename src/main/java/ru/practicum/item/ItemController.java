package ru.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.User;
import ru.practicum.user.UserService;

import static ru.practicum.item.ItemMapper.toDto;
import static ru.practicum.item.ItemMapper.toEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @GetMapping
    public List<ItemDto> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId).stream().map(ItemMapper::toDto).toList();
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam("text") String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemService.getItems(userId, text).stream().map(ItemMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable("id") Long itemId) {
        Optional<Item> item = itemService.getItem(userId, itemId);
        return item.map(value -> new ResponseEntity<>(toDto(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody ItemDto itemDto) {

        if (itemDto.getAvailable() == null) {
            return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
        }

        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            return new ResponseEntity<>(itemDto, HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userService.getUser(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Item item = toEntity(itemDto);
        item.setUserId(userId);
        return new ResponseEntity<>(toDto(itemService.addNewItem(item)), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("id") Long itemId,
                                          @RequestBody ItemDto itemDto) {
        Optional<User> user = userService.getUser(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Item> itemOpt = itemService.getItem(userId, itemId);
        if (itemOpt.isEmpty()) {
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

        item.setUserId(userId);
        return new ResponseEntity<>(toDto(itemService.updateItem(item)), HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable(name = "itemId") long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}