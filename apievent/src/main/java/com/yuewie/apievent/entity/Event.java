package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;



@Getter
@Setter
@ToString(exclude = "liens")
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

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LienEventAdresse> liens = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addLien(LienEventAdresse lien) {
        lien.setEvent(this); // On recolle le parent (indispensable avec le Mapper)
        this.liens.add(lien);
    }

    public void removeLien(Adresse adresse) {
        // On cherche le lien qui pointe vers cette adresse et on le dégage
        // C'est 100% sûr car on compare l'ID de l'adresse (fiable)
        this.liens.removeIf(lien -> lien.getAdresse().equals(adresse));
    }


}
