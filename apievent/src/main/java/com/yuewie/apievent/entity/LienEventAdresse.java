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
    @ToString.Exclude
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
        if (this.id != null && this.id.getEventId() != null && this.id.getAdresseId() != null) {
            return Objects.equals(this.id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
