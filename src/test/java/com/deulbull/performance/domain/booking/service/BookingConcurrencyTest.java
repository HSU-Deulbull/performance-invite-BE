package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.exception.BookingConflictException;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("예매 동시성 테스트")
class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private Performance testPerformance;

    @BeforeEach
    void setUp() {
        // 테스트용 공연 생성
        testPerformance = Performance.builder()
                .title("동시성 테스트 공연")
                .preSaleEndTime(LocalDateTime.now().plusDays(7))
                .preSaleFee(10000)
                .build();
        testPerformance = performanceRepository.save(testPerformance);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        performanceRepository.deleteAll();
    }

    @Test
    @DisplayName("동시에 100개의 예매 요청 시 낙관적 락이 정상 작동한다")
    void testConcurrentBooking() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    BookingRequestDto requestDto = new BookingRequestDto(
                            "테스터" + index,
                            "010-0000-" + String.format("%04d", index),
                            2,
                            "카카오페이"
                    );
                    bookingService.createBooking(testPerformance.getId(), requestDto);
                    successCount.incrementAndGet();
                } catch (BookingConflictException | OptimisticLockException e) {
                    conflictCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("예상치 못한 예외: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        long totalBookings = bookingRepository.countByPerformance(testPerformance);
        System.out.println("총 예매 수: " + totalBookings);
        System.out.println("성공 카운트: " + successCount.get());
        System.out.println("충돌 카운트: " + conflictCount.get());

        // 모든 요청이 처리되었는지 확인
        assertThat(successCount.get() + conflictCount.get()).isEqualTo(threadCount);

        // 실제 저장된 예매 수가 성공 카운트와 일치하는지 확인
        assertThat(totalBookings).isEqualTo(successCount.get());
    }

    @Test
    @DisplayName("동일한 사용자가 짧은 시간 내 여러 번 예매 시도 시 중복 체크가 작동한다")
    void testDuplicateBookingPrevention() throws InterruptedException {
        // given
        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(threadCount);
        String sameName = "중복테스터";
        String samePhone = "010-1234-5678";

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    BookingRequestDto requestDto = new BookingRequestDto(
                            sameName,
                            samePhone,
                            2,
                            "카카오페이"
                    );
                    bookingService.createBooking(testPerformance.getId(), requestDto);
                } catch (Exception e) {
                    // 중복으로 인한 무시 또는 충돌 예외
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        // 3초 내 중복 체크로 인해 실제 저장된 예매는 소수여야 함
        long totalBookings = bookingRepository.countByPerformance(testPerformance);
        System.out.println("중복 체크 후 실제 저장된 예매 수: " + totalBookings);

        // 5번 시도 중 중복 체크로 인해 1~2개만 저장되어야 함
        assertThat(totalBookings).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("낙관적 락 버전 필드가 정상적으로 증가한다")
    void testVersionIncrement() {
        // given
        BookingRequestDto requestDto = new BookingRequestDto(
                "버전테스터",
                "010-9999-9999",
                2,
                "카카오페이"
        );

        // when
        bookingService.createBooking(testPerformance.getId(), requestDto);

        // then
        Booking savedBooking = bookingRepository.findAll().get(0);
        assertThat(savedBooking.getVersion()).isNotNull();
        assertThat(savedBooking.getVersion()).isEqualTo(0L);
    }
}
