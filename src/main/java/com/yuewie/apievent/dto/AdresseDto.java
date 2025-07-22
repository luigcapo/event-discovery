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
    private String intituleAdresse;
    private String codePostal;
    private String ville;
    private String pays;
}
