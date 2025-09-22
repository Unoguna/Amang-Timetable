package com.amang_timetable.domain.reservation.reservation.controller;

import com.amang_timetable.domain.reservation.reservation.dto.ReservationForm;
import com.amang_timetable.domain.reservation.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CalendarController {
    private final ReservationService reservationService;

    @MessageMapping("/updateCell")
    @SendTo("/topic/calendar")
    public ReservationForm updateCalendar(ReservationForm msg) {
        try {
            // DB 저장
            if (msg.isPersist()) {
                if (msg.getReserver() == null || msg.getReserver().isBlank()) {
                    // ✅ 예약자명이 비어 있으면 삭제
                    reservationService.deleteReservation(msg.getDate(), msg.getStartTime());
                } else {
                    // ✅ 예약자명이 있으면 저장/업데이트
                    reservationService.createOrUpdateReservation(msg);
                }
            }
            return msg; // 항상 브로드캐스트 (UI 업데이트)
        } catch (Exception e) {
            // 실패했을 때는 에러 메시지 객체를 따로 만들어서 내려주거나 로그만 찍는 방식 사용
            return new ReservationForm(msg.getDate(), "00:00", "00:00" ,"에러: " + e.getMessage(), false, "null");
        }
    }
}
