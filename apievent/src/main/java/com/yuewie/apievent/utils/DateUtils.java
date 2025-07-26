package com.yuewie.apievent.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class DateUtils {

    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ISO_LOCAL_DATE;    // 2025‑05‑18
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("HH:mm"); // 14:30

    public static LocalDateTime convert(String date, String heure){
        LocalDate localDate = LocalDate.parse(date, DATE_FMT);
        if (heure == null || heure.isBlank()) {
            return localDate.atStartOfDay();
        }
        LocalTime localTime = LocalTime.parse(heure, TIME_FMT);
        return LocalDateTime.of(localDate, localTime);
    }

}
