package com.amang_timetable.domain.reservation.reservation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationForm {
    private String date;      // yyyy-MM-dd 형식
    private String startTime; // HH:mm
    private String endTime;   // HH:mm
    private String reserver;
}
