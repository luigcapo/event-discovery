package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "{entity.event.name.notblank}")
    @Size(min = 3, max = 255, message = "{entity.event.name.size}")
    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "{entity.event.start.notnull}")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @NotNull(message = "{entity.event.end.notnull}")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LienEventAdresse> liens = new HashSet<>();


    public void addLien(LienEventAdresse lien) {
        lien.setEvent(this); // On recolle le parent (indispensable avec le Mapper)
        this.liens.add(lien);
    }

    public void removeLien(Adresse adresse) {
        this.liens.removeIf(lien -> lien.getAdresse().equals(adresse));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Event event)) return false;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
