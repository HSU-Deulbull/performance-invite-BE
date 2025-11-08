package com.deulbull.performance.domain.booking.repository;

import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.performance.entity.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 특정 공연의 예매 목록 조회 (페이징, createdAt 기준 정렬은 Pageable로 처리)
    Page<Booking> findByPerformance(Performance performance, Pageable pageable);

    // 특정 공연의 총 예매 건수
    long countByPerformance(Performance performance);

    // 특정 공연의 총 인원 수 합계
    @Query("SELECT COALESCE(SUM(b.headCount), 0) FROM Booking b WHERE b.performance = :performance")
    Integer sumHeadCountByPerformance(@Param("performance") Performance performance);

    List<Booking> findAllByPerformanceId(Long performanceId);
}
