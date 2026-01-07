package com.yuewie.apievent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LienEventAdresseRequestDto {

    private boolean principal;

    @NotNull(message = "{lien.adresse.not.null}")
    @Valid
    private AdresseRequestDto adresse;
}
