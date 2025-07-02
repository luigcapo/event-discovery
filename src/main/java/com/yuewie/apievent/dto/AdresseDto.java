package com.yuewie.apievent.dto;

import com.yuewie.apievent.entity.Event;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@Builder
public class AdresseDto {
    private Long id;

    @NonNull
    private String intituleAdresse;

    @NonNull
    private String codePostal;

    @NonNull
    private String ville;

    @NonNull
    private String pays;
}
