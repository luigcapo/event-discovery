package com.yuewie.apievent.repository.impl;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Adresse;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.repository.EventCriteriaApiRepository;
import com.yuewie.apievent.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class EventCriteriaApiRepositoryImpl implements EventCriteriaApiRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> findAllCriteaApi(EventSearchCriteria eventSearchCriteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);

        //on utilise pas le cq.select cat on veut prendre toutes les colonnes. ON pourrait l'utiliser si
        // on veut des colonnes spécifiques
        Root<Event> root = cq.from(Event.class);
        Join<Event, Adresse> adresseJoin = root.joinSet("adresses", JoinType.INNER);

        List<Predicate> predicateList = new ArrayList<>();
        if (eventSearchCriteria.getName() != null && !eventSearchCriteria.getName().isBlank()) {
            predicateList.add(cb.like(cb.lower(root.get("name")), "%" + eventSearchCriteria.getName().toLowerCase() + "%"));
        }
        if (eventSearchCriteria.getVille() != null && !eventSearchCriteria.getVille().isBlank()) {
            predicateList.add(cb.like(cb.lower(adresseJoin.get("ville")), "%" + eventSearchCriteria.getVille().toLowerCase() + "%"));
        }

        if (eventSearchCriteria.getCodePostal() != null && !eventSearchCriteria.getCodePostal().isBlank()) {
            predicateList.add(cb.equal(cb.lower(adresseJoin.get("codePostal")), eventSearchCriteria.getCodePostal().toLowerCase()));
        }

        if (eventSearchCriteria.getIntituleAdresse() != null && !eventSearchCriteria.getIntituleAdresse().isBlank()) {
            predicateList.add(cb.like(cb.lower(adresseJoin.get("intituleAdresse")), "%" + eventSearchCriteria.getIntituleAdresse().toLowerCase() + "%"));
        }

        if (eventSearchCriteria.getStartDate() != null && !eventSearchCriteria.getStartDate().isBlank()) {
            LocalDateTime startDateTime = DateUtils.convert(eventSearchCriteria.getStartDate(), eventSearchCriteria.getStartTime());
            predicateList.add(cb.greaterThanOrEqualTo(root.get("start"), startDateTime));
        }

        if (eventSearchCriteria.getEndDate() != null && !eventSearchCriteria.getEndDate().isBlank()) {
            LocalDateTime endDateTime = DateUtils.convert(eventSearchCriteria.getEndDate(), eventSearchCriteria.getEndTime());
            predicateList.add(cb.lessThanOrEqualTo(root.get("end"), endDateTime));
        }


        cq.select(root).distinct(true).where(predicateList.toArray(new Predicate[0]));

        if (eventSearchCriteria.getOrderBy() != null) {

            Path<?> orderByPath = root.get(String.valueOf(eventSearchCriteria.getOrderBy()));
            // si on rajoute un orderby sur les champs de l'adresse, il faudrait faire adresseJoin.get(...)
            // Exemple simple : if (eventSearchCriteria.getOrderBy().startsWith("adresse.")) {
            // orderByPath = adresseJoin.get(eventSearchCriteria.getOrderBy().substring("adresse.".length()));
            // } else {
            // orderByPath = eventRoot.get(eventSearchCriteria.getOrderBy());
            // }


            if (eventSearchCriteria.getOrderDirection() != null && "DESC".equalsIgnoreCase(String.valueOf(eventSearchCriteria.getOrderDirection()))) {
                cq.orderBy(cb.desc(orderByPath));
            } else {
                cq.orderBy(cb.asc(orderByPath));
            }
        }

        TypedQuery<Event> query = entityManager.createQuery(cq);

        query.setFirstResult((eventSearchCriteria.getPageNumber() - 1) * eventSearchCriteria.getPageSize());
        query.setMaxResults(eventSearchCriteria.getPageSize());
        return query.getResultList();
    }

    @Override
    public Event createCriteriaApi(Event event) {
        return null;
    }

    @Override
    public void updateCriteriaApi(Long eventId, Event event) {

    }

    @Override
    public void deleteCriteriaApi(Long eventId) {

    }
}
