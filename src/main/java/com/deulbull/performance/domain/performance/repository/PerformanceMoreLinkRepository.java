package com.deulbull.performance.domain.performance.repository;

import com.deulbull.performance.domain.performance.entity.PerformanceMoreLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceMoreLinkRepository extends JpaRepository<PerformanceMoreLink, Long> {
    List<PerformanceMoreLink> findAllByPerformanceId(Long id);
}
