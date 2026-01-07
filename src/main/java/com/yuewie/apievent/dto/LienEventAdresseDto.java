package com.yuewie.apievent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LienEventAdresseDto {
    private boolean principal;
    private AdresseDto adresse;
}
