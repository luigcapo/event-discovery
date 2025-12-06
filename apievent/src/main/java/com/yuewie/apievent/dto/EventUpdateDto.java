package com.yuewie.apievent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(
        name = "EventUpdate",
        description = "Données pour le remplacement complet d'un événement (PUT). Tous les champs sont obligatoires."
)
public class EventUpdateDto {

    @NotBlank
    @Size(min = 3, message = "Le nom de l'événement ne peut êtrer inférieur à 3 caractères")
    @Schema(description = "Nouveau nom de l'événement", example = "Gala de Charité (Reporté)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Nouvelle description", example = "Reporté suite aux intempéries.")
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Nouvelle date de début", example = "01-01-2026 20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime start;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Nouvelle date de fin", example = "02-01-2026 04:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime end;
}
