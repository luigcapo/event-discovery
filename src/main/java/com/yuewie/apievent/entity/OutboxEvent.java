package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "outbox_event")
@NoArgsConstructor // Required for JPA
@AllArgsConstructor // Full constructor for convenience
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "kafka_key", nullable = false)
    private String kafkaKey; // e.g., "event-123"

    @Lob
    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
