package com.yuewie.apievent.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AdresseRequestDto {
    //Pas beosin du jsonIgnore comme dans l'event ici car je ne veux pas modifier une adresse existante.
    //Si l'adresse existe déjà, on ne la modifie pas, on en crée une nouvelle.
    //RIsque sinon de modifier l'adresse d'un événement existant.

    @NonNull
    private String intituleAdresse;

    @NonNull
    private String codePostal;

    @NonNull
    private String ville;

    @NonNull
    private String pays;
}
