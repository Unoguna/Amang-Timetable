package com.amang_timetable.domain.reservation.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationForm {
    private String date;      // yyyy-MM-dd 형식
    private String startTime; // HH:mm
    private String endTime;   // HH:mm
    private String reserver;
    private boolean persist;
}
