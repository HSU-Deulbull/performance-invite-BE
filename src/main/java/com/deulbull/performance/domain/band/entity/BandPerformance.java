package com.deulbull.performance.domain.band.entity;

import com.deulbull.performance.domain.performance.entity.Performance;
import jakarta.persistence.*;
import lombok.*;

// 공연 별 참가 밴드
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BandPerformance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "band_id")
    private Band band;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

}
