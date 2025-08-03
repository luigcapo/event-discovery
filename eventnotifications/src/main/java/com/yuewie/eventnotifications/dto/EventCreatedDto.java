package com.yuewie.eventnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Nécessaire pour la désérialisation
@AllArgsConstructor
@Builder
public class EventCreatedDto {
    private Long id;
    private String name;
    private String start;
    private String end;
}
