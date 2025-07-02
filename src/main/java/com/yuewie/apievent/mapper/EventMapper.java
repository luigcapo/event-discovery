package com.yuewie.apievent.mapper;

import com.yuewie.apievent.dto.EventDto;
import com.yuewie.apievent.entity.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto toDto(Event event);
    Event toEntity(EventDto eventDto);
}
