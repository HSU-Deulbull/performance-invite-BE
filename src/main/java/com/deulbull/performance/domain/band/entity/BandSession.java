package com.deulbull.performance.domain.band.entity;

import com.deulbull.performance.domain.band.entity.enums.SessionType;
import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SessionType sessionType; // 세션

    @ManyToOne
    @JoinColumn(name = "performance_song_id")
    private PerformanceSong performanceSong; // 공연별 곡 정보

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band band; // 밴드 정보

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person; // 참여자 정보


}
