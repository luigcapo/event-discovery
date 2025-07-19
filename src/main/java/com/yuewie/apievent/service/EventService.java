package com.yuewie.apievent.service;

import com.yuewie.apievent.dto.EventDto;
import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.aop.log.Loggable;

import java.util.List;

public interface EventService {
    List<EventDto> findAllEvent();
    List<EventDto> searchEventUsingJpql(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingCriteria(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingSpecification(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingNativeSql(EventSearchCriteria eventSearchCriteria);
    List<EventDto> searchEventUsingQueryDSL(EventSearchCriteria eventSearchCriteria);


    EventDto createEvent(EventDto eventDto);

    EventDto updateEvent(Long eventId, EventDto eventDto);

    void deleteEvent(Long eventId);

    EventDto getEvent(Long id);
}
