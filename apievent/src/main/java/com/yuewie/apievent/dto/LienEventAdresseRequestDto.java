package com.yuewie.apievent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LienEventAdresseRequestDto {

    private boolean principal;

    @NotNull(message = "L'adresse est obligatoire")
    @Valid
    private AdresseRequestDto adresse;
}
