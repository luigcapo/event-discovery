package com.yuewie.apievent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(
        name = "EventCreation",
        description = "Données requises pour créer un nouvel événement."
)
public class EventCreateDto {

    @NotNull
    @Size(min = 3, message = "{event.name.size}")
    @Schema(description = "Nom de l'événement", example = "Gala de Charité", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Description optionnelle", example = "Collecte de fonds annuelle...")
    private String description;

    @NotNull
    @FutureOrPresent(message = "{event.start.future}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Date de début (doit être future)", example = "25-12-2025 20:00:00", type = "string", pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime start;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(description = "Date de fin", example = "26-12-2025 04:00:00", type = "string", pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime end;

    @NotEmpty(message = "{event.adresses.not.empty}")
    @Schema(description = "Liste des adresses initiales de l'événement")
    private Set<@Valid LienEventAdresseRequestDto> adresses;

    @AssertTrue(message = "{event.date.period.invalid}")
    @JsonIgnore
    public boolean isDatePeriodValid() {
        if (start == null || end == null) {
            return true;
        }
        return end.isAfter(start);
    }
}
