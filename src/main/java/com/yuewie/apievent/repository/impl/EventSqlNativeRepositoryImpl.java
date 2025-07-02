package com.yuewie.apievent.repository.impl;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.repository.EventSqlNativeRepository;
import com.yuewie.apievent.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class EventSqlNativeRepositoryImpl implements EventSqlNativeRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * on utilise le ? position parameter ici au lieu de :paramname (le name parameter). LE POSITIONAL EST PRÉFÉRÉ pour les natives query.
     * BOn c'est ce qui est souvent fait mais clairement moi je pense en bonne pratique go paramname :)
     * @param eventSearchCriteria
     * @return
     */

    @Override
    public List<Event> findAllNativeSQL(EventSearchCriteria eventSearchCriteria) {
        StringBuilder sql = new StringBuilder("Select DISTINCT e.* FROM event e");
        StringBuilder whereClause = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        int paramIndex = 1; // Les paramètres positionnels en SQL natif commencent à 1
        // Jointure si des critères sur l'adresse sont présents ou si on trie par un champ d'adresse
        boolean joinAdresse = (eventSearchCriteria.getVille() != null && !eventSearchCriteria.getVille().isBlank()) ||
                (eventSearchCriteria.getCodePostal() != null && !eventSearchCriteria.getCodePostal().isBlank()) ||
                (eventSearchCriteria.getIntituleAdresse() != null && !eventSearchCriteria.getIntituleAdresse().isBlank());
        if (joinAdresse) {
            sql.append(" JOIN lien_adresse_event l ON l.event_id = e.id JOIN adresse a ON l.adresse_id = a.id");
        }

        if (eventSearchCriteria.getName() != null && !eventSearchCriteria.getName().isBlank()) {
            whereClause.append(" AND LOWER(e.name) LIKE LOWER(?)");
            params.add("%" + eventSearchCriteria.getName() + "%");
        }

        if (eventSearchCriteria.getVille() != null && !eventSearchCriteria.getVille().isBlank()) {
            if (!joinAdresse) {
                whereClause.append(" AND LOWER(a.ville) LIKE LOWER(?)");
                params.add("%" + eventSearchCriteria.getVille() + "%");
            }
        }

        if (eventSearchCriteria.getCodePostal() != null && !eventSearchCriteria.getCodePostal().isBlank()) {
            if (!joinAdresse) { sql.append(" JOIN adresse a ON e.adresse_id = a.id"); joinAdresse = true;}
            whereClause.append(" AND LOWER(a.code_postal) = LOWER(?)");
            params.add(eventSearchCriteria.getCodePostal());
        }

        if (eventSearchCriteria.getIntituleAdresse() != null && !eventSearchCriteria.getIntituleAdresse().isBlank()) {
            if (!joinAdresse) { sql.append(" JOIN adresse a ON e.adresse_id = a.id"); joinAdresse = true;}
            whereClause.append(" AND LOWER(a.intitule_adresse) LIKE LOWER(?)");
            params.add("%" + eventSearchCriteria.getIntituleAdresse() + "%");
        }

        if (eventSearchCriteria.getStartDate() != null && !eventSearchCriteria.getStartDate().isBlank()) {
            LocalDateTime startDateTime = DateUtils.convert(eventSearchCriteria.getStartDate(), eventSearchCriteria.getStartTime());
            whereClause.append(" AND e.start_date >= ?").append(paramIndex++);
            params.add(startDateTime);//JDBC SAIT CONVERTIT localdatetime en string
        }

        if (eventSearchCriteria.getEndDate() != null && !eventSearchCriteria.getEndDate().isBlank()) {
            LocalDateTime endDateTime = DateUtils.convert(eventSearchCriteria.getEndDate(), eventSearchCriteria.getEndTime());
            whereClause.append(" AND e.end_date <= ?").append(paramIndex++);
            params.add(endDateTime);
        }

        sql.append(whereClause);

        // --- Gestion du tri (ORDER BY) ---
        // **ATTENTION SÉCURITÉ MAJEURE ICI**
        // Injecter directement eventSearchCriteria.getOrderBy() est DANGEREUX (risque d'injection SQL).
        // Il faut utiliser une liste blanche (whitelist) de colonnes autorisées pour le tri.
        if (eventSearchCriteria.getOrderBy() != null) {
            String orderByField = mapToSafeColumnForOrderBy(eventSearchCriteria.getOrderBy().toString(), joinAdresse); // Méthode à implémenter
            if (orderByField != null) {
                sql.append(" ORDER BY ").append(orderByField); // orderByField DOIT être une colonne validée et sûre
                String direction = " ASC"; // Défaut
                if (eventSearchCriteria.getOrderDirection() != null && "DESC".equalsIgnoreCase(eventSearchCriteria.getOrderDirection().toString())) {
                    direction = " DESC";
                }
                sql.append(direction);
            }
        }

        // Création de la requête native.
        Query query = em.createNativeQuery(sql.toString(), Event.class);

        sql.append(" LIMIT ? OFFSET ?");
        params.add(eventSearchCriteria.getPageSize());
        params.add((eventSearchCriteria.getPageNumber() - 1) * eventSearchCriteria.getPageSize());

        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i)); // Les paramètres sont 1-indexed (alors que la plupart des objet tab sont 0indexed)
        }

        return query.getResultList();
    }

    @Override
    public Event createNativeSQL(Event event) {
        return null;
    }

    @Override
    public void updateCNativeSQL(Long eventId, Event event) {

    }

    @Override
    public void deleteNativeSQL(Long eventId) {

    }
    /**
     * Méthode CRUCIALE pour la sécurité du tri.
     * Mappe un champ de tri de l'API (ex: "name", "adresse.ville") à un nom de colonne SQL sûr.
     * Empêche l'injection SQL.
     */
    private String mapToSafeColumnForOrderBy(String orderByFieldFromCriteria, boolean joinPerformed) {
        if (orderByFieldFromCriteria == null || orderByFieldFromCriteria.isBlank()) {
            return null; // Pas de tri
        }
        String field = orderByFieldFromCriteria.toLowerCase();
        switch (field) {
            case "name":
                return "e.name";
            case "start": // Supposons que le critère de tri 'start' correspond à 'start_datetime'
                return "e.start_date";
            case "end":
                return "e.end_date";
            // Pour les champs de l'adresse, s'assurer que la jointure a été faite
            case "adresse.ville":
                return joinPerformed ? "a.ville" : null; // Ne pas trier par ville si pas de jointure
            case "adresse.codepostal":
                return joinPerformed ? "a.code_postal" : null;
            // Ajoutez d'autres colonnes de tri autorisées ici
            default:
                // Nom de champ non reconnu ou non autorisé pour le tri
                // Loggez une alerte si nécessaire, et ne retournez rien pour éviter l'injection.
                System.err.println("Tentative de tri sur un champ non autorisé : " + orderByFieldFromCriteria);
                return null;
        }
    }

}
