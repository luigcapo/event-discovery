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
    private String name;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private Set<AdresseDto> adresses;
}
