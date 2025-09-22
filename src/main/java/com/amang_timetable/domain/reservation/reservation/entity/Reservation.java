package com.amang_timetable.domain.reservation.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(
        name = "reservations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"date", "start_time"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Builder   // ✅ 추가해야 builder() 메서드 자동 생성됨
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    private String reserver;
}
