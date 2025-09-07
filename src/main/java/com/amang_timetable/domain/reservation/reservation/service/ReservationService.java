package com.amang_timetable.domain.reservation.reservation.service;

import com.amang_timetable.domain.reservation.reservation.dto.ReservationForm;
import com.amang_timetable.domain.reservation.reservation.entity.Reservation;
import com.amang_timetable.domain.reservation.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public List<Reservation> getReservationsForMonth(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return reservationRepository.findByDateBetween(start, end);
    }

    @Transactional
    public void createReservation(ReservationForm form) {
        Reservation reservation = Reservation.builder()                .date(LocalDate.parse(form.getDate()))
                .startTime(LocalTime.parse(form.getStartTime()))
                .endTime(LocalTime.parse(form.getEndTime()))
                .reserver(form.getReserver())
                .build();

        reservationRepository.save(reservation);
    }
}
