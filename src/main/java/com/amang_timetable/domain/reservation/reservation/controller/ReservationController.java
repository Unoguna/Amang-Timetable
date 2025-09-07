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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/reservations")
    public String getReservations(@RequestParam(required = false) Integer year,
                                  @RequestParam(required = false) Integer month,
                                  Model model) {
        YearMonth ym = (year == null || month == null) ? YearMonth.now() : YearMonth.of(year, month);

        LocalDate firstDay = ym.atDay(1);
        LocalDate lastDay = ym.atEndOfMonth();

        LocalDate start = firstDay.with(java.time.DayOfWeek.SUNDAY);
        if (start.isAfter(firstDay)) start = start.minusWeeks(1);

        LocalDate end = lastDay.with(java.time.DayOfWeek.SATURDAY);
        if (end.isBefore(lastDay)) end = end.plusWeeks(1);

        // 주 리스트
        List<List<LocalDate>> weeks = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); ) {
            List<LocalDate> w = new ArrayList<>(7);
            for (int i = 0; i < 7; i++, d = d.plusDays(1)) w.add(d);
            weeks.add(w);
        }

        // 월 범위 예약 읽기
        List<Reservation> reservations = reservationService.getReservationsForMonth(ym);

        // 날짜→시간→리스트 맵 (모든 칸을 빈 리스트로 초기화)
        Map<String, Map<String, List<Reservation>>> reservationMap = new HashMap<>();

// 빈 칸 선채움
        for (List<LocalDate> week : weeks) {
            for (LocalDate d : week) {
                String dateKey = d.toString();
                Map<String, List<Reservation>> hourMap = reservationMap.computeIfAbsent(dateKey, k -> new HashMap<>());
                for (int h = 5; h <= 23; h++) {
                    hourMap.putIfAbsent(String.valueOf(h), new ArrayList<>());
                }
            }
        }

// 데이터 넣기
        for (Reservation r : reservations) {
            String dateKey = r.getDate().toString();
            Map<String, List<Reservation>> hourMap = reservationMap.get(dateKey);
            if (hourMap == null) continue;
            for (int h = r.getStartTime().getHour(); h < r.getEndTime().getHour(); h++) {
                hourMap.get(String.valueOf(h)).add(r);
            }
        }

        System.out.println(reservations);
        System.out.println(reservationMap);

        model.addAttribute("yearMonth", ym);
        model.addAttribute("weeks", weeks);
        model.addAttribute("reservationMap", reservationMap);
        return "reservations";
    }


    // inline 입력 저장 (엑셀처럼)
    @PostMapping("/reservations/inline")
    @ResponseBody
    public ResponseEntity<?> createReservationInline(@RequestBody ReservationForm form) {
        try {
            reservationService.createReservation(form);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("예약 실패: " + e.getMessage());
        }
    }
}
