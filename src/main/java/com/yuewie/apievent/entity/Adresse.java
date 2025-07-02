package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@Table(name = "adresse")
@NoArgsConstructor // Required for JPA
@AllArgsConstructor // Full constructor for convenience
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intituleAdresse", nullable = false)
    private String intituleAdresse;

    @Column(name = "codePostal", nullable = false)
    private String codePostal;

    @Column(name = "ville", nullable = false)
    private String ville;

    @Column(name="pays", nullable = false)
    private String pays;

    @ManyToMany(mappedBy = "adresses")
    Set<Event> events;
}
