package com.yuewie.apievent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Builder
@Schema(name = "AdresseResponse", description = "Détails d'une adresse physique.")
public class AdresseDto {

    @Schema(description = "Identifiant technique de l'adresse", example = "42")
    private Long id;

    @Schema(description = "Numéro de voie", example = "10 Bis")
    private String numero;

    @Schema(description = "Nom de la voie", example = "Rue de la Paix")
    private String rue;

    @Schema(description = "Code postal", example = "75002")
    private String codePostal;

    @Schema(description = "Ville", example = "Paris")
    private String ville;

    @Schema(description = "Pays", example = "France")
    private String pays;
}
