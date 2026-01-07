package com.yuewie.apievent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Schema(
        name = "EventResponse",
        description = "Représentation complète d'un événement (lecture)."
)
public class EventDto {

    @Schema(description = "Identifiant unique de l'événement", example = "123")
    private Long id;

    @Schema(description = "Nom de l'événement", example = "Festival Rock 2025")
    private String name;

    @Schema(description = "Description détaillée", example = "Un grand concert en plein air avec des groupes locaux.")
    private String description;

    @Schema(description = "Date et heure de début", example = "2025-07-14 18:00:00")
    private LocalDateTime start;

    @Schema(description = "Date et heure de fin", example = "2025-07-15 02:00:00")
    private LocalDateTime end;

    @Schema(description = "Liste de toutes les adresses liées à l'événement avec leur statut (principale ou non)")
    private Set<LienEventAdresseDto> adresses;

    @Schema(description = "Raccourci vers l'adresse principale (pour affichage liste/carte)")
    private AdresseDto adressePrincipale;
}
