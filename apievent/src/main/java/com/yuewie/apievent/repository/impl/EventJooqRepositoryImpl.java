package com.yuewie.apievent.repository.impl;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.repository.EventJooqRepository;
import com.yuewie.apievent.utils.DateUtils;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Repository
public class EventJooqRepositoryImpl implements EventJooqRepository {

    private final DSLContext dsl;

     @Autowired
    public EventJooqRepositoryImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Event> findAllJooq() {
//        return new HashSet<>(dsl.select().from().fetchInto(Event.class));
        return List.of();
    }

    @Override
    public List<Event> findAllJooq(EventSearchCriteria eventSearchCriteria) {
//        var query = dsl.select()
//                .from("event")
//                .join("adresse").on("event.id = adresse.event_id");
//
//        if (StringUtils.hasText(eventSearchCriteria.getName())) {
//            query.where(DSL.lower(DSL.field("event.name")).like("%" + eventSearchCriteria.getName().toLowerCase() + "%"));
//        }
//
//        if (StringUtils.hasText(eventSearchCriteria.getVille())) {
//            query.where(DSL.lower(DSL.field("adresse.ville")).like("%" + eventSearchCriteria.getVille().toLowerCase() + "%"));
//        }
//
//        if (eventSearchCriteria.getCodePostal() != null && !eventSearchCriteria.getCodePostal().isBlank()) {
//            query.where(DSL.lower(DSL.field("adresse.codePostal")).eq(eventSearchCriteria.getCodePostal().toLowerCase()));
//        }
//
//        if (eventSearchCriteria.getIntituleAdresse() != null && !eventSearchCriteria.getIntituleAdresse().isBlank()) {
//            query.where(DSL.lower(DSL.field("adresse.intituleAdresse")).like("%" + eventSearchCriteria.getIntituleAdresse().toLowerCase() + "%"));
//        }
//
//        if (eventSearchCriteria.getStartDate() != null && !eventSearchCriteria.getStartDate().isBlank()) {
//            LocalDateTime startDateTime = DateUtils.convert(eventSearchCriteria.getStartDate(), eventSearchCriteria.getStartTime());
//            query.where(DSL.field("event.start").ge(startDateTime));
//        }
//
//        if (eventSearchCriteria.getEndDate() != null && !eventSearchCriteria.getEndDate().isBlank()) {
//            LocalDateTime endDateTime = DateUtils.convert(eventSearchCriteria.getEndDate(), eventSearchCriteria.getEndTime());
//            query.where(DSL.field("event.end").le(endDateTime));
//        }
//
//        if (eventSearchCriteria.getOrderBy() != null) {
//            var orderByField = DSL.field("event." + eventSearchCriteria.getOrderBy());
//            if ("DESC".equalsIgnoreCase(eventSearchCriteria.getOrderDirection())) {
//                query.orderBy(orderByField.desc());
//            } else {
//                query.orderBy(orderByField.asc());
//            }
//        }
//
//        query.limit(eventSearchCriteria.getPageSize())
//                .offset((eventSearchCriteria.getPageNumber() - 1) * eventSearchCriteria.getPageSize());
//
//        return new HashSet<>(query.fetchInto(Event.class));
        return List.of();
    }

    @Override
    public Event createJooq(Event event) {
        return null;
    }

    @Override
    public void updateJooq(Long eventId, Event event) {

    }

    @Override
    public void deleteJooq(Long eventId) {

    }
}
