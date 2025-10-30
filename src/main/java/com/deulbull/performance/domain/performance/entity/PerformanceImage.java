package com.deulbull.performance.domain.performance.entity;

import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceImage extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl; // 이미지 URL

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;
}
