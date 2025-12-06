package com.yuewie.apievent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(
        name = "EventPatch",
        description = "Modèle pour la mise à jour partielle d'un événement. " +
                "Tous les champs sont optionnels. Seuls les champs non-nulls seront modifiés."
)
public class EventPatchDto {

    @Size(min = 3, message = "Le nom de l'événement ne peut être inférieur à 3 caractères")
    @Schema(example = "Concert Rock", description = "Nouveau nom de l'événement(optionnel)")
    private String name;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(example = "12-07-2025 12:39:17", description = "Format: dd-MM-yyyy HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(example = "12-07-2025 12:39:17", description = "Format: dd-MM-yyyy HH:mm:ss")
    private LocalDateTime end;
}
