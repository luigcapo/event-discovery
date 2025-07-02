package com.yuewie.apievent.repository;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;

import java.util.List;
import java.util.Set;

public interface EventQueryDSLRepository {
    List<Event> findAllQueryDsl(EventSearchCriteria eventSearchCriteria);
}
