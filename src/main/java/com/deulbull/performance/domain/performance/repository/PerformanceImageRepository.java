package com.deulbull.performance.domain.performance.repository;

import com.deulbull.performance.domain.performance.entity.PerformanceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceImageRepository extends JpaRepository<PerformanceImage, Long> {
    List<PerformanceImage> findAllByPerformanceId(Long performance_id);
}
