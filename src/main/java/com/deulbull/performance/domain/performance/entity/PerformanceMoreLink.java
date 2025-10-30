package com.deulbull.performance.domain.performance.entity;

import com.deulbull.performance.domain.performance.entity.enums.LinkType;
import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceMoreLink extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LinkType type;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;
}
