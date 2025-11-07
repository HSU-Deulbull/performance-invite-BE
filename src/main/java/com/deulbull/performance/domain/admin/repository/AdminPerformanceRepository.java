package com.deulbull.performance.domain.admin.repository;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.song.projection.CurrentSongProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPerformanceRepository extends JpaRepository<Admin, Long> {
    @Query("""
                select
                    s.title as title,
                    s.artist      as artist,
                    s.albumImgUrl as albumImgUrl
                from Admin a
                    join a.performance p
                    join p.currentSong ps
                    join ps.song s
                where a.id = :adminId
            """)
    Optional<CurrentSongProjection> findCurrentSongDtoByAdminId(Long adminId);
}
