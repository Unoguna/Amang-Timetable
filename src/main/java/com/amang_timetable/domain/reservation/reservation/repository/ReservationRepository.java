package com.amang_timetable.domain.reservation.reservation.repository;

import com.amang_timetable.domain.reservation.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateBetween(LocalDate start, LocalDate end);
}
