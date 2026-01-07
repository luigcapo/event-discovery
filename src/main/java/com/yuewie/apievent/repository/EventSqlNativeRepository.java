package com.yuewie.apievent.repository;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;

import java.util.List;
import java.util.Set;

public interface EventSqlNativeRepository {
    List<Event> findAllNativeSQL(EventSearchCriteria eventSearchCriteria);
    Event createNativeSQL(Event event);
    void updateCNativeSQL(Long eventId, Event event);
    void deleteNativeSQL(Long eventId);

}
