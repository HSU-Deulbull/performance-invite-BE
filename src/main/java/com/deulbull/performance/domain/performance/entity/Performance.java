package com.deulbull.performance.domain.performance.entity;

import com.deulbull.performance.domain.performanceSong.entity.PerformanceSong;
import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Performance extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    private String description;
    private String location; // 주소 (ex. 서울 서대문구 연세로5다길 10 지하1층)
    private String venue; // 공연장 이름 (ex. 신촌 몽향)
    private LocalTime time;
    private LocalDate date;
    private Integer preSaleFee;   // 사전예매 가격
    private Integer onSiteFee;    // 현장예매 가격
    private String posterUrl;
    private String openchatUrl;

    @ManyToOne
    @JoinColumn(name = "current_song_id")
    private PerformanceSong currentSong; // 현재 진행중인 곡
}
