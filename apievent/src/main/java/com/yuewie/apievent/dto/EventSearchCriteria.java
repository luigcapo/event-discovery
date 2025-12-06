package com.yuewie.apievent.dto;

import com.yuewie.apievent.dto.constraint.EventFieldForOrderBy;
import com.yuewie.apievent.dto.constraint.OrderDirection;
import com.yuewie.apievent.entity.Adresse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(
        name = "EventSearch",
        description = "Critères de recherche multi-critères pour les événements."
)
public class EventSearchCriteria {

    @Schema(description = "Recherche partielle sur le nom de l'événement (insensible à la casse)", example = "Rock")
    private String name;

    @Schema(description = "Filtre par ville", example = "Paris")
    private String ville;

    @Schema(description = "Filtre exact par code postal", example = "75012")
    private String codePostal;

    @Schema(description = "Filtre exact par numero de rue", example = "2bis")
    private String numero;

    @Schema(description = "Filtre exact par nom de rue", example = "avenue du general pierre")
    private String rue;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "startDate doit respecter le format yyyy‑MM‑dd"
    )
    @Schema(description = "Date de début minimum (Format: yyyy-MM-dd)", example = "2025-07-01")
    private String startDate;

    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "startTime doit respecter le format HH:mm (0–23:59)"
    )
    @Schema(description = "Heure de début minimum (Format: HH:mm)", example = "18:00")
    private String startTime;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "endDate doit respecter le format yyyy‑MM‑dd"
    )
    @Schema(description = "Date de fin maximum (Format: yyyy-MM-dd)", example = "2025-07-31")
    private String endDate;

    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "endTime doit respecter le format HH:mm (0–23:59)"
    )
    @Schema(description = "Heure de fin maximum (Format: HH:mm)", example = "23:59")
    private String endTime;

    @Schema(description = "Champ de tri", defaultValue = "id")
    private EventFieldForOrderBy orderBy = EventFieldForOrderBy.id;

    @Schema(description = "Direction du tri (ASC/DESC)", defaultValue = "ASC")
    private OrderDirection orderDirection = OrderDirection.ASC;

    @Min(value = 1, message = "La page doit commencer à 1")
    @Schema(description = "Numéro de la page (commence à 1)", defaultValue = "1", minimum = "1")
    private int pageNumber = 1;

    @Min(value = 1, message = "La taille de page doit être au moins 1")
    @Max(value = 100, message = "La taille de page est limitée à 100")  // Sécurité anti-DoS
    @Schema(description = "Nombre d'éléments par page", defaultValue = "10", maximum = "100")
    private int pageSize = 10;
}
