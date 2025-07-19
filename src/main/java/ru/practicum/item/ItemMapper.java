package ru.practicum.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.booking.BookingMapper;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toDto(Item item) {
        if (item == null) return null;
        ItemDto itemDto =  new ItemDto(item.getId(), item.getUserId(), item.getUrl(), item.getAvailable(), item.getDescription(), item.getName());
        itemDto.setLastBooking(BookingMapper.toDto(item.getLastBooking()));
        itemDto.setNextBooking(BookingMapper.toDto(item.getNextBooking()));

        List<CommentDto> commentDtos = null;
        if (item.getComments() != null) {
            commentDtos = item.getComments().stream().map(CommentMapper::toDto).toList();
        }

        itemDto.setComments(commentDtos);
        itemDto.setRequestId(item.getRequestId());
        return itemDto;
    }

    public static ItemShortDto toShortDto(Item item) {
        if (item == null) return null;
        return new ItemShortDto(item.getId(), item.getName());
    }

    public static Item toEntity(ItemDto itemDto) {
        if (itemDto == null) return null;
        return new Item(itemDto.getId(), itemDto.getUserId(), itemDto.getUrl(), itemDto.getAvailable(), itemDto.getDescription(), itemDto.getName());
    }
}