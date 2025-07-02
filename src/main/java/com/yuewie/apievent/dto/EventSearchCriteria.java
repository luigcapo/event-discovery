package com.yuewie.apievent.dto;

import com.yuewie.apievent.dto.constraint.EventFieldForOrderBy;
import com.yuewie.apievent.dto.constraint.OrderDirection;
import com.yuewie.apievent.entity.Adresse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EventSearchCriteria {
    private String name;

    private String ville;

    private String codePostal;

    private String intituleAdresse;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "startDate doit respecter le format yyyy‑MM‑dd"
    )
    private String startDate;

    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "startTime doit respecter le format HH:mm (0–23:59)"
    )
    private String startTime;

    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "endDate doit respecter le format yyyy‑MM‑dd"
    )
    private String endDate;

    @Pattern(
            regexp = "^([01]\\d|2[0-3]):[0-5]\\d$",
            message = "endTime doit respecter le format HH:mm (0–23:59)"
    )
    private String endTime;

    private EventFieldForOrderBy orderBy = EventFieldForOrderBy.id;

    private OrderDirection orderDirection = OrderDirection.ASC;

    private int pageNumber = 0;

    private int pageSize = 10;
}
