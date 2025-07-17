package ru.practicum.request;

import ru.practicum.item.ItemMapper;
import ru.practicum.item.ItemShortDto;

import java.util.ArrayList;
import java.util.List;

public class RequestMapper {
    public static Request toEntity(RequestDto requestDto) {
        return new Request(requestDto.getDescription());
    }

    public static RequestDto toDto(Request request) {
        RequestDto requestDto = new RequestDto(request.getId(), request.getDescription(), request.getCreated());
        List<ItemShortDto> itemDtos = new ArrayList<>();
        if (request.getItems() != null) {
            itemDtos = request.getItems().stream().map(ItemMapper::toShortDto).toList();
        }
        requestDto.setItems(itemDtos);
        return requestDto;
    }
}
