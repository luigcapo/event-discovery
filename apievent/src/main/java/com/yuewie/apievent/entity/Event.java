package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Entity
@Table(name = "event")
@NoArgsConstructor // Required for JPA
@AllArgsConstructor // Full constructor for convenience
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "lien_adresse_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "adresse_id")
    )
    @Column(nullable = false)
    @Size(min = 1, message = "Un événement doit avoir au moins une adresse")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Adresse> adresses;
}
