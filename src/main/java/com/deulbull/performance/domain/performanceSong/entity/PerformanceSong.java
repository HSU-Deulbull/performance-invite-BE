package com.deulbull.performance.domain.performanceSong.entity;

import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 공연 별 셋리스트
@Entity
public class PerformanceSong extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
