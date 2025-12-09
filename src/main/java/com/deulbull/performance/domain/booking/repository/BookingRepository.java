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

    // 특정 공연의 예매 목록 조회 - 이름 검색 (페이징)
    Page<Booking> findByPerformanceAndNameContaining(Performance performance, String name, Pageable pageable);

    // 특정 공연의 총 예매 건수 - 단순 엔티티 수 반환
    long countByPerformance(Performance performance);

    // 특정 공연의 총 예매 건수 - 이름 검색
    long countByPerformanceAndNameContaining(Performance performance, String name);

    // 특정 공연의 총 인원 수 합계
    @Query("SELECT COALESCE(SUM(b.headCount), 0) FROM Booking b WHERE b.performance = :performance")
    Integer sumHeadCountByPerformance(@Param("performance") Performance performance);

    // 특정 공연의 총 인원 수 합계 - 이름 검색
    @Query("SELECT COALESCE(SUM(b.headCount), 0) FROM Booking b WHERE b.performance = :performance AND b.name LIKE CONCAT('%', :name, '%')")
    Integer sumHeadCountByPerformanceAndNameContaining(@Param("performance") Performance performance, @Param("name") String name);

    List<Booking> findAllByPerformanceId(Long performanceId);

    // 중복 예매 방지: 특정 시간 이후 같은 이름+전화번호로 예매한 내역이 있는지 확인
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.performance.id = :performanceId " +
           "AND b.name = :name AND b.phoneNumber = :phoneNumber " +
           "AND b.createdAt > :afterTime")
    boolean existsRecentBooking(
            @Param("performanceId") Long performanceId,
            @Param("name") String name,
            @Param("phoneNumber") String phoneNumber,
            @Param("afterTime") java.time.LocalDateTime afterTime
    );
}
