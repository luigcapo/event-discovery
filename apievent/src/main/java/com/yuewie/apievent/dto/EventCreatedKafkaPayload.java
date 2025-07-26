package com.yuewie.apievent.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreatedKafkaPayload {
    private Long id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
}
