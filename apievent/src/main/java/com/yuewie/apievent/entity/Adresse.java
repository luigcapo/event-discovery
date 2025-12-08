package com.yuewie.apievent.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "adresse")
@NoArgsConstructor // Required for JPA
@AllArgsConstructor // Full constructor for convenience
public class Adresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 10, message = "{entity.adresse.numero.size}")
    @Column(name = "numero", length = 10)
    private String numero; // Ex: "10", "12 Bis"

    @NotBlank(message = "{entity.adresse.rue.notblank}")
    @Column(name = "rue", nullable = false)
    private String rue;

    @NotBlank(message = "{entity.adresse.codepostal.notblank}")
    @Column(name = "codePostal", nullable = false)
    private String codePostal;

    @NotBlank(message = "{entity.adresse.ville.notblank}")
    @Column(name = "ville", nullable = false)
    private String ville;

    @NotBlank(message = "{entity.adresse.pays.notblank}")
    @Column(name="pays", nullable = false)
    private String pays;


//    Enlever car risque de pb de perf si par exemple 10000 event dans l'année pour une adresse. Si besoin faire une requete avec pagination
//    @OneToMany(mappedBy = "adresse")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Set<LienAdresseEvent> events = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Adresse adresse)) return false;
        return getId() != null && Objects.equals(getId(), adresse.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
