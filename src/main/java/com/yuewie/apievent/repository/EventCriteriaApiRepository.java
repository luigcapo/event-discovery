package com.yuewie.apievent.repository;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;

import java.util.List;
import java.util.Set;

public interface EventCriteriaApiRepository {
    List<Event> findAllCriteaApi(EventSearchCriteria eventSearchCriteria);
    Event createCriteriaApi(Event event);
    void updateCriteriaApi(Long eventId, Event event);
    void deleteCriteriaApi(Long eventId);
}
