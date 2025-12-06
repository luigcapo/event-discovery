package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "lien_event_adresse")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LienEventAdresse {

    @EmbeddedId
    @Builder.Default
    private LienEventAdresseId id = new LienEventAdresseId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId") // Mappe la partie 'eventId' de la clé composite
    @JoinColumn(name = "event_id")
    @ToString.Exclude //sinon on rapelera to string sur LienEventAdresse creant to string infini
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId("adresseId") // Mappe la partie 'adresseId' de la clé composite
    @JoinColumn(name = "adresse_id")
    private Adresse adresse;

    @Column(name = "is_principal")
    private boolean isPrincipal;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LienEventAdresse that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
