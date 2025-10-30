package com.deulbull.performance.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // LocalDateTime을 날짜(요일) 시간으로 변환해주는 메서드
    // LocalDateTime -> MM/dd(요일) HH:mm
    public String formatDateTimeWithDay(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        String day = DAY_MAP.get(dateTime.getDayOfWeek()); // 요일
        String date = dateTime.format(DateTimeFormatter.ofPattern("MM/dd"));
        String time = dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        // 날짜(요일) 시간
        return String.format("%s(%s) %s", date, day, time);
    }
    private static Map<DayOfWeek, String> DAY_MAP = Map.of(
            DayOfWeek.MONDAY, "월",
            DayOfWeek.TUESDAY, "화",
            DayOfWeek.WEDNESDAY, "수",
            DayOfWeek.THURSDAY, "목",
            DayOfWeek.FRIDAY, "금",
            DayOfWeek.SATURDAY, "토",
            DayOfWeek.SUNDAY, "일"
    );
}
