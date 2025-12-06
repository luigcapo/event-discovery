package com.yuewie.apievent.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LienEventAdresseDto {
    private boolean principal;
    private AdresseDto adresse;
}
