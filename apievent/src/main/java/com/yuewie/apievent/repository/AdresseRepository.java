package com.yuewie.apievent.repository;

import com.yuewie.apievent.entity.Adresse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdresseRepository extends JpaRepository<Adresse, Long> {
    Optional<Adresse> findByNumeroAndRueAndCodePostalAndVilleAndPays(
            String numero, String rue, String codePostal, String ville, String pays
    );
}
