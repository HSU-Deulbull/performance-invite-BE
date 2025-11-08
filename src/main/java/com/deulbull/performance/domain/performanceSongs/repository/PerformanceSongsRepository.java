package com.deulbull.performance.domain.performanceSongs.repository;

import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceSongsRepository extends JpaRepository<PerformanceSong, Long> {
    Optional<PerformanceSong> findWithSongById(Long id);

    List<PerformanceSong> findByPerformanceId(Long id);
}
