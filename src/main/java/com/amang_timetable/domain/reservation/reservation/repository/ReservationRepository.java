package com.amang_timetable.domain.reservation.reservation.repository;

import com.amang_timetable.domain.reservation.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateBetween(LocalDate start, LocalDate end);
    Optional<Reservation> findByDateAndStartTime(LocalDate date, LocalTime startTime);

}
