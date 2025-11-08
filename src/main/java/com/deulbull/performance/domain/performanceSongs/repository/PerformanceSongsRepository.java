package com.deulbull.performance.domain.performanceSongs.repository;

import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import com.deulbull.performance.domain.song.projection.CurrentSongProjection;
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

    // 다음 곡: current.orderInPerformance보다 큰 곡 중 가장 작은 순서
    Optional<PerformanceSong> findFirstByPerformance_IdAndOrderInPerformanceGreaterThanOrderByOrderInPerformanceAsc(
            Long performanceId, Integer orderInPerformance
    );

    // 첫 곡: 현재곡이 없거나 마지막에서 다음이 없을 때
    Optional<PerformanceSong> findFirstByPerformance_IdOrderByOrderInPerformanceAsc(Long performanceId);

    // 이전 곡: 현재 순번보다 작은 것 중 가장 큰 순번
    Optional<PerformanceSong>
    findFirstByPerformance_IdAndOrderInPerformanceLessThanOrderByOrderInPerformanceDesc(
            Long performanceId, Integer orderInPerformance
    );

    // 마지막 곡
    Optional<PerformanceSong>
    findFirstByPerformance_IdOrderByOrderInPerformanceDesc(Long performanceId);

    // ps.id로 프로젝션 1건 찾기
    @Query("""
        select s.title as title, s.artist as artist, s.albumImgUrl as albumImgUrl
        from PerformanceSong ps
        join ps.song s
        where ps.id = :performanceSongId
    """)
    Optional<CurrentSongProjection> findProjectionById(@Param("performanceSongId") Long performanceSongId);
}
