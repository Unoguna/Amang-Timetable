package com.amang_timetable.domain.reservation.reservation.config;

import com.amang_timetable.domain.reservation.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final ReservationService reservationService;

    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시
    public void cleanOldReservations() {
        reservationService.cleanupOldReservations();
    }
}
