package com.yuewie.apievent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuewie.apievent.entity.Adresse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class EventDto {

    private Long id;

    @NotNull
    @Size(min = 3, message = "Le nom de l'événement ne peut êtrer inférieur à 3 caractères")
    private String name;

    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(example = "12-07-2025 12:39:17", description = "Format: dd-MM-yyyy HH:mm:ss")
    private LocalDateTime start;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    @Schema(example = "12-07-2025 12:39:17", description = "Format: dd-MM-yyyy HH:mm:ss")
    private LocalDateTime end;

    @NotEmpty(message = "L'événement doit contenir au moins une adresse")
    private Set<@Valid AdresseDto> adresses;
}
