package com.yuewie.apievent.repository.impl;

import com.yuewie.apievent.dto.EventSearchCriteria;
import com.yuewie.apievent.entity.Adresse;
import com.yuewie.apievent.entity.Event;
import com.yuewie.apievent.entity.LienEventAdresse;
import com.yuewie.apievent.utils.DateUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EventSpecifications {

    private static Join<Event, Adresse> adresseJoin(Root<Event> root, JoinType type) {
        // 1. Récupérer ou créer la jointure vers le LIEN
        Join<Event, LienEventAdresse> lienJoin = root.getJoins().stream()
                .filter(j -> "adresses".equals(j.getAttribute().getName()))
                .map(j -> (Join<Event, LienEventAdresse>) j)
                .findFirst()
                .orElseGet(() -> root.join("adresses", type));

        // 2. Retourner la jointure vers l'ADRESSE (c'est ce que les specs attendent pour filtrer sur la ville)
        return lienJoin.join("adresse", type);
    }

    public static Specification<Event> byName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Toujours vrai si pas de nom
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Event> byVille(String ville) {
        return (root, query, criteriaBuilder) -> {
            if (ville == null || ville.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Event, Adresse> adresseJoin = adresseJoin(root, JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(adresseJoin.get("ville")), "%" + ville.toLowerCase() + "%");
        };
    }

    public static Specification<Event> byCodePostal(String codePostal) {
        return (root, query, criteriaBuilder) -> {
            if (codePostal == null || codePostal.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Event, Adresse> adresseJoin = adresseJoin(root, JoinType.INNER);
            return criteriaBuilder.equal(criteriaBuilder.lower(adresseJoin.get("codePostal")), codePostal.toLowerCase());
        };
    }

    public static Specification<Event> byNumero(String numero) {
        return (root, query, criteriaBuilder) -> {
            if (numero == null || numero.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Event, Adresse> adresseJoin = adresseJoin(root, JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(adresseJoin.get("numero")), "%" + numero.toLowerCase() + "%");
        };
    }

    public static Specification<Event> byRue(String rue) {
        return (root, query, criteriaBuilder) -> {
            if (rue == null || rue.isBlank()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            Join<Event, Adresse> adresseJoin = adresseJoin(root, JoinType.INNER);
            return criteriaBuilder.like(criteriaBuilder.lower(adresseJoin.get("rue")), "%" + rue.toLowerCase() + "%");
        };
    }

    public static Specification<Event> byDateDebut(LocalDateTime startDateTime) {
        return (root, query, criteriaBuilder) -> {
            if (startDateTime == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("start"), startDateTime);
        };
    }

    public static Specification<Event> byDateFin(LocalDateTime endDateTime) {
        return (root, query, criteriaBuilder) -> {
            if (endDateTime == null) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("end"), endDateTime);
        };
    }

    // Méthode pour construire la spécification globale à partir des critères
    public static Specification<Event> creerSpecification(EventSearchCriteria eventSearchCriteria) {
        Specification<Event> spec = Specification.where(null); // Commence avec une spécification neutre toujours vrai pour démarrer la construction des spec

        if (eventSearchCriteria.getName() != null && !eventSearchCriteria.getName().isBlank()) {
            spec = spec.and( byName(eventSearchCriteria.getName()) );
        }
        if (eventSearchCriteria.getVille() != null && !eventSearchCriteria.getVille().isBlank()) {
            spec = spec.and( byVille(eventSearchCriteria.getVille()) );
        }
        if (eventSearchCriteria.getCodePostal() != null && !eventSearchCriteria.getCodePostal().isBlank()) {
            spec = spec.and( byCodePostal(eventSearchCriteria.getCodePostal()) );
        }
        if (eventSearchCriteria.getNumero() != null && !eventSearchCriteria.getNumero().isBlank()) {
            spec = spec.and( byNumero(eventSearchCriteria.getNumero()) );
        }
        if (eventSearchCriteria.getRue() != null && !eventSearchCriteria.getRue().isBlank()) {
            spec = spec.and( byRue(eventSearchCriteria.getRue()) );
        }
        if (eventSearchCriteria.getStartDate() != null && !eventSearchCriteria.getStartDate().isBlank()) {
            LocalDateTime startDateTime = DateUtils.convert(eventSearchCriteria.getStartDate(), eventSearchCriteria.getStartTime());
            spec = spec.and( byDateDebut(startDateTime) );
        }
        if (eventSearchCriteria.getEndDate() != null && !eventSearchCriteria.getEndDate().isBlank()) {
            LocalDateTime endDateTime = DateUtils.convert(eventSearchCriteria.getEndDate(), eventSearchCriteria.getEndTime());
            spec = spec.and( byDateFin(endDateTime) );
        }
        return spec.and((root, q, cb) -> {          // DISTINCT pour éviter les doublons
            q.distinct(true);
            return cb.conjunction();
        });
    }


}
