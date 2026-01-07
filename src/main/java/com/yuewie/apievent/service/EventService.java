package com.yuewie.apievent.service;

import com.yuewie.apievent.dto.*;
import com.yuewie.apievent.dto.constraint.EventFieldForOrderBy;
import com.yuewie.apievent.dto.constraint.OrderDirection;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface EventService {
    List<EventDto> findAllEvent();
    List<EventDto> searchEventUsingJpql(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingCriteria(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingSpecification(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingNativeSql(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingQueryDSL(EventSearchCriteria eventSearchCriteria);


    EventDto createEvent(EventCreateDto eventDto);

    EventDto updateEvent(Long eventId, EventUpdateDto eventDto);

    EventDto patchEvent(Long eventId, EventPatchDto updateDto);

    void deleteEvent(Long eventId);

    EventDto getEvent(Long id);

    EventDto createEventWithEnvoieKafka(EventCreateDto eventDto);

    EventDto addAdresse(Long eventId, LienEventAdresseRequestDto dto);

    void removeAdresse(Long eventId, Long adresseId);

    Set<LienEventAdresseDto> getAdresseByEventId(Long eventId);

    Page<EventDto> findEvents(int page, int size, EventFieldForOrderBy orderBy, OrderDirection direction);
}
