package com.yuewie.apievent.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Hidden
public class EventCreatedKafkaPayload {
    private Long id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
}
