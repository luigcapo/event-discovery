package com.yuewie.apievent.service;

import com.yuewie.apievent.dto.*;

import java.util.List;

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
}
