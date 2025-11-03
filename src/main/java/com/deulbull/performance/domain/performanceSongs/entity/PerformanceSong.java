package com.deulbull.performance.domain.performanceSongs.entity;

import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.song.entity.Song;
import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

// 공연 별 셋리스트
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceSong extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer orderInPerformance; // 공연 내 순서
    private int likes; // 공연별 곡 좋아요 수

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance; // 공연 정보

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song; // 곡 정보
}
