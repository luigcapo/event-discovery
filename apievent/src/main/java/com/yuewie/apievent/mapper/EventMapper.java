package com.yuewie.apievent.mapper;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.entity.Event;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {AdresseMapper.class})
public interface EventMapper {
    EventDto toDto(Event event);
    Event toEntity(EventCreateDto eventCreateDto);
    Event toEntity(EventUpdateDto eventUpdateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventPatchDto dto, @MappingTarget Event entity);
    EventCreatedKafkaPayload toKafkaPayload(EventDto eventDto);

}
