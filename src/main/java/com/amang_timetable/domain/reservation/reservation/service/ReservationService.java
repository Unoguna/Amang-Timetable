package com.amang_timetable.domain.reservation.reservation.service;

import com.amang_timetable.domain.reservation.reservation.dto.ReservationForm;
import com.amang_timetable.domain.reservation.reservation.entity.Reservation;
import com.amang_timetable.domain.reservation.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public List<Reservation> getReservationsBetween(LocalDate start, LocalDate end) {
        return reservationRepository.findByDateBetween(start, end);
    }

    public void createOrUpdateReservation(ReservationForm form) {
        LocalDate date = LocalDate.parse(form.getDate());
        LocalTime startTime = LocalTime.parse(form.getStartTime());

        // 같은 날짜+시간 슬롯에 예약이 있는지 확인
        Optional<Reservation> existing = reservationRepository
                .findByDateAndStartTime(date, startTime);

        if (existing.isPresent()) {
            // ✅ 수정
            Reservation r = existing.get();
            r.setReserver(form.getReserver());
            r.setEndTime(LocalTime.parse(form.getEndTime()));
            reservationRepository.save(r);
        } else {
            // ✅ 신규 생성
            Reservation r = Reservation.builder()
                    .date(LocalDate.parse(form.getDate()))
                    .startTime(LocalTime.parse(form.getStartTime()))
                    .endTime(LocalTime.parse(form.getEndTime()))
                    .reserver(form.getReserver())
                    .build();
            reservationRepository.save(r);
        }
    }

    public void deleteReservation(String date, String startTime) {
        reservationRepository.deleteByDateAndStartTime(LocalDate.parse(date), LocalTime.parse(startTime));
    }

    public void cleanupOldReservations() {
        LocalDate cutoffDate = LocalDate.now().minusYears(1);
        reservationRepository.deleteByDateBefore(cutoffDate);
        System.out.println("✅ 1년 전 이전 데이터 삭제 완료: " + cutoffDate + " 이전");
    }

}
