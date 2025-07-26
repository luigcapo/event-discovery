package com.yuewie.apievent.repository.impl;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.repository.EventJpqlRepository;
import com.yuewie.apievent.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

/**
 * SI obliger d'utiliser JPQL
 * Normalement pour les petite query on fait un query inline
 * Sinon si trop complexe utiliser typequery
 * A noter que namequery peut etre utiliser sur l'entite
 * qui est plus rapide que le query car charge
 * directement dans le cache plan au demarrage ce qui n'est pas des query/typequery
 * qui attendent le premier lancer de la methode pour etre mis dans le plan de cache.
 * Aussi les problèmes sur le namequery sont détectés directement à la compilation contrairement
 * au query
 */
@Repository
public class EventJpqlRepositoryImpl implements EventJpqlRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Event> findAllJpql(EventSearchCriteria eventSearchCriteria) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder jpql = new StringBuilder("Select DISTINCT e FROM Event e JOIN e.adresses a WHERE 1=1");

        if (eventSearchCriteria.getName() != null && !eventSearchCriteria.getName().isBlank()) {
            jpql.append(" AND LOWER(e.name) LIKE LOWER(:name)");
            params.put("name", "%" + eventSearchCriteria.getName() + "%");
        }

        if (eventSearchCriteria.getVille() != null && !eventSearchCriteria.getVille().isBlank()) {
            jpql.append(" AND LOWER(a.ville) LIKE LOWER(:ville)");
            params.put("ville", "%" + eventSearchCriteria.getVille() + "%");
        }

        if (eventSearchCriteria.getCodePostal() != null && !eventSearchCriteria.getCodePostal().isBlank()) {
            jpql.append(" AND LOWER(acodePostal) = LOWER(:codePostal)");
            params.put("codePostal", eventSearchCriteria.getCodePostal());
        }

        if (eventSearchCriteria.getIntituleAdresse() != null && !eventSearchCriteria.getIntituleAdresse().isBlank()) {
            jpql.append(" AND LOWER(a.intituleAdresse) LIKE LOWER(:intituleAdresse)");
            params.put("intituleAdresse", "%" + eventSearchCriteria.getIntituleAdresse() + "%");
        }

        if (eventSearchCriteria.getStartDate() != null && !eventSearchCriteria.getStartDate().isBlank()){
            LocalDateTime startDateTime;
            startDateTime =   DateUtils.convert(eventSearchCriteria.getStartDate(), eventSearchCriteria.getStartTime());
            jpql.append(" AND e.start >= :startDateTime");
            params.put("startDateTime",startDateTime);
        }

        if (eventSearchCriteria.getEndDate() != null && !eventSearchCriteria.getEndDate().isBlank()){
            LocalDateTime endDateTime;
            endDateTime =   DateUtils.convert(eventSearchCriteria.getEndDate(), eventSearchCriteria.getEndTime());
            jpql.append(" AND e.end <= :endDateTime");
            params.put("endDateTime",endDateTime);
        }

        if(eventSearchCriteria.getOrderBy() != null){
            jpql.append(" ORDER BY e.").append(eventSearchCriteria.getOrderBy());
            if(eventSearchCriteria.getOrderDirection() != null){
                jpql.append(" " + eventSearchCriteria.getOrderDirection());
            }
            else{
                jpql.append(" ASC");
            }
        }

        TypedQuery<Event> query = entityManager.createQuery(jpql.toString(), Event.class);
        params.forEach( (k, v) -> {query.setParameter(k,v);});
        query.setFirstResult((eventSearchCriteria.getPageNumber() - 1) * eventSearchCriteria.getPageSize());
        query.setMaxResults(eventSearchCriteria.getPageSize());
        return query.getResultList();
    }
}
