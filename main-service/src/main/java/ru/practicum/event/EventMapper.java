package ru.practicum.event;

import org.mapstruct.Mapper;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.service.CategoryServiceImpl;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring", uses = {CategoryServiceImpl.class, CategoryMapper.class})
public interface EventMapper {

    Event toEvent(NewEventDto newEventDto);

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
