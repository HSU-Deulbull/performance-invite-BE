package com.deulbull.performance.domain.performanceSongs.repository;

import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceSongsRepository extends JpaRepository<PerformanceSong, Long> {
    Optional<PerformanceSong> findWithSongById(Long id);
    @Modifying
    @Query(value = "update performance_song set likes = greatest(likes+:delta, 0) where id = :id", nativeQuery = true)
    int likes(@Param("id") Long id, @Param("delta") int delta);

    Optional<LikesOnly> findLikesById(Long performanceSongId);

    List<PerformanceSong> findByPerformanceId(Long id);
}
