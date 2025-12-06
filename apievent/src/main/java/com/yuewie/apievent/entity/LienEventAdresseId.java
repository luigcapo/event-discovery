package com.yuewie.apievent.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LienEventAdresseId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long adresseId;

}
