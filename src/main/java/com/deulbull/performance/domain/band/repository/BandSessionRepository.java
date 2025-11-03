package com.deulbull.performance.domain.band.repository;

import com.deulbull.performance.domain.band.entity.BandSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BandSessionRepository extends JpaRepository<BandSession, Long> {
    @Query("""
           select bs
           from BandSession bs
           left join fetch bs.person p
           where bs.performanceSong.id = :performanceSongId
           order by bs.sessionType asc, p.name asc
           """)
    List<BandSession> findAllByPerformanceSongIdWithPerson(Long performanceSongId);
}
