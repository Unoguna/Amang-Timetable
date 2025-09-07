package com.amang_timetable.domain.reservation.reservation.controller;

import com.amang_timetable.domain.reservation.reservation.dto.ReservationForm;
import com.amang_timetable.domain.reservation.reservation.entity.Reservation;
import com.amang_timetable.domain.reservation.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/reservations")
    public String getReservations(@RequestParam(required = false) Integer year,
                                  @RequestParam(required = false) Integer month,
                                  Model model) {
        YearMonth ym = (year == null || month == null)
                ? YearMonth.now()
                : YearMonth.of(year, month);

        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();

        // 달력 시작을 "이 달의 첫 일요일"로, 끝을 "이 달의 마지막 토요일"로 맞추기
        LocalDate start = firstDay.with(java.time.DayOfWeek.SUNDAY);
        if (start.isAfter(firstDay)) {
            start = start.minusWeeks(1);
        }
        LocalDate end = lastDay.with(java.time.DayOfWeek.SATURDAY);
        if (end.isBefore(lastDay)) {
            end = end.plusWeeks(1);
        }

        // 주 단위 리스트 만들기
        List<List<LocalDate>> weeks = new ArrayList<>();
        LocalDate day = start;
        while (!day.isAfter(end)) {
            List<LocalDate> week = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                week.add(day);
                day = day.plusDays(1);
            }
            weeks.add(week);
        }

        List<Reservation> reservations = reservationService.getReservationsForMonth(ym);

        model.addAttribute("yearMonth", ym);
        model.addAttribute("weeks", weeks); // ✅ 주 단위 날짜
        model.addAttribute("reservations", reservations);
        model.addAttribute("form", new ReservationForm());

        return "reservations";
    }


    @PostMapping("/reservations")
    public String createReservation(@ModelAttribute("form") ReservationForm form) {
        reservationService.createReservation(form);
        return "redirect:/reservations"; // 등록 후 다시 목록으로
    }

    @PostMapping("/reservations/inline")
    @ResponseBody
    public ResponseEntity<?> createReservationInline(@RequestBody ReservationForm form) {
        reservationService.createReservation(form);
        return ResponseEntity.ok().build();
    }

}
