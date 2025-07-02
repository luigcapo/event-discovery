package com.yuewie.apievent.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.entity.QEvent;
import com.yuewie.apievent.repository.EventQueryDSLRepository;
import com.yuewie.apievent.utils.DateUtils;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class EventQueryDSLRepositoryImpl implements EventQueryDSLRepository {

    private final JPAQueryFactory queryFactory;

    public EventQueryDSLRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Event> findAllQueryDsl(EventSearchCriteria criteria) {
        QEvent event = QEvent.event;

        BooleanBuilder where = new BooleanBuilder();

        /* --- filtres dynamiques ---------------------------------------- */
        if (StringUtils.hasText(criteria.getName())) {
            where.and(event.name.containsIgnoreCase(criteria.getName()));
        }
        if (StringUtils.hasText(criteria.getVille())) {
            where.and(event.adresses.any().ville.containsIgnoreCase(criteria.getVille()));
        }
        if (StringUtils.hasText(criteria.getCodePostal())) {
            where.and(event.adresses.any().codePostal.equalsIgnoreCase(criteria.getCodePostal()));
        }
        if (StringUtils.hasText(criteria.getIntituleAdresse())) {
            where.and(event.adresses.any().intituleAdresse.containsIgnoreCase(criteria.getIntituleAdresse()));
        }

        if (StringUtils.hasText(criteria.getStartDate())) {
            LocalDateTime start = DateUtils.convert(criteria.getStartDate(), criteria.getStartTime());
            where.and(event.start.goe(start));
        }
        if (StringUtils.hasText(criteria.getEndDate())) {
            LocalDateTime end = DateUtils.convert(criteria.getEndDate(), criteria.getEndTime());
            where.and(event.end.loe(end));
        }

        /* --- construction de la requête -------------------------------- */
        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .where(where);

        /* --- tri dynamique --------------------------------------------- */
        if (criteria.getOrderBy() != null) {
            PathBuilder<Event> path = new PathBuilder<>(Event.class, "event");
            Order direction = ("DESC".equalsIgnoreCase(criteria.getOrderDirection().toString()))
                    ? Order.DESC : Order.ASC;
            query.orderBy(new OrderSpecifier<>(direction, path.getString(criteria.getOrderBy().toString()) ) );
        }

        /* --- pagination ------------------------------------------------ */
        int offset = (criteria.getPageNumber() - 1) * criteria.getPageSize();
        query.offset(offset)
                .limit(criteria.getPageSize());

        /* --- exécution -------------------------------------------------- */
        return query.fetch();
    }
}
