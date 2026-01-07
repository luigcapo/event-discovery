package com.yuewie.apievent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(name = "AdresseInput", description = "Structure d'une adresse pour la création ou la modification.")
public class AdresseRequestDto {

    @NotBlank
    @Schema(description = "Numéro de voie (facultatif)", example = "15 Ter")
    private String numero;

    @NotBlank
    @Schema(description = "Nom de la voie", example = "Avenue Victor Hugo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rue;

    @NotBlank
    @Schema(description = "Code postal (5 chiffres)", example = "69002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codePostal;

    @NotBlank
    @Schema(description = "Ville", example = "Lyon", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ville;

    @NotBlank
    @Schema(description = "Pays", example = "France", requiredMode = Schema.RequiredMode.REQUIRED)
    private String pays;
}
