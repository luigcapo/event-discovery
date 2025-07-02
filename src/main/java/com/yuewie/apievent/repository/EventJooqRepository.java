package com.yuewie.apievent.repository;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;

import java.util.List;
import java.util.Set;

public interface EventJooqRepository {
    List<Event> findAllJooq();
    List<Event> findAllJooq(EventSearchCriteria eventSearchCriteria);
    Event createJooq(Event event);
    void updateJooq(Long eventId, Event event);
    void deleteJooq(Long eventId);
}
