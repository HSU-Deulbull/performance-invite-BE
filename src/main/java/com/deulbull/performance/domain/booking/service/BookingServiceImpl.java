package com.deulbull.performance.domain.booking.service;

import com.deulbull.performance.domain.admin.service.AdminMessageService;
import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.exception.BookingDeadlinePassedException;
import com.deulbull.performance.domain.booking.exception.BookingNotFoundException;
import com.deulbull.performance.domain.booking.exception.OpenChatUrlNotFoundException;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.booking.web.dto.BookingRequestDto;
import com.deulbull.performance.domain.booking.web.dto.BookingUpdateRequestDto;
import com.deulbull.performance.domain.booking.web.dto.PreBookingInfoResponse;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.exception.PerformanceNotFoundException;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.deulbull.performance.global.discord.DiscordWebhookSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    private final BookingRepository bookingRepository;
    private final PerformanceRepository performanceRepository;
    private final AdminMessageService adminMessageService;
    private final DiscordWebhookSender discordWebhookSender;

    @Override
    @Transactional
    public synchronized void createBooking(Long performanceId, BookingRequestDto requestDto) {
        // 1. 공연 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);

        // 2. 예매 마감 기한 확인 (preSaleEndTime 기준)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime preSaleEndTime = performance.getPreSaleEndTime();

        if (preSaleEndTime != null && now.isAfter(preSaleEndTime)) {
            throw new BookingDeadlinePassedException();
        }

        // 3. 중복 예매 방지: 최근 3초 이내 동일 이름+전화번호 예매 체크
        int seconds = 3;
        LocalDateTime fewSecondsAgo = now.minusSeconds(seconds);
        boolean isDuplicate = bookingRepository.existsRecentBooking(
                performanceId,
                requestDto.name(),
                requestDto.phoneNumber(),
                fewSecondsAgo
        );

        if (isDuplicate) {
            // 중복 감지 시: DB 저장하지 않고 로깅과 디스코드 알림만 전송
            log.warn("[중복 예매 감지] performanceId={}, name={}, phone={}, headCount={} - 3초 이내 중복 요청으로 저장하지 않음",
                    performanceId,
                    requestDto.name(),
                    requestDto.phoneNumber(),
                    requestDto.headCount()
            );

            String duplicateMessage = String.format(
                    "## [⚠️중복 예매 감지 - 저장 안 됨]\n" +
                            "이름: **%s**\n" +
                            "연락처: %s\n" +
                            "인원: %d명\n" +
                            "시간: %s\n" +
                            "사유: %d초 이내 중복 요청\n" +
                            "==========================================",
                    requestDto.name(),
                    requestDto.phoneNumber(),
                    requestDto.headCount(),
                    now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    seconds
            );
            discordWebhookSender.sendBooking(duplicateMessage);

            // TODO: 추후 프론트 측 중복 방지 완료 시 예외를 던져서 사용자에게 명확히 알리는 방식으로 변경 고려
            return; // 성공 응답 반환 (프론트 에러 방지)
        }

        // 3. 예매 생성
        Booking booking = Booking.builder()
                .name(requestDto.name())
                .phoneNumber(requestDto.phoneNumber())
                .headCount(requestDto.headCount())
                .performance(performance)
                .build();
        bookingRepository.save(booking);

        // 4. 콘솔 로그
        int totalPrice = (performance.getPreSaleFee() != null ? performance.getPreSaleFee() : 0) * requestDto.headCount();
        log.info("[예매 생성] performanceId={}, name={}, phone={}, headCount={}, paymentMethod={}, totalPrice={}",
                performanceId,
                requestDto.name(),
                requestDto.phoneNumber(),
                requestDto.headCount(),
                requestDto.paymentMethod(),
                totalPrice
        );

        Long totalBookingCount = bookingRepository.countByPerformance(performance);
        // 5. discord 웹훅 알림
        String discordMessage = String.format(
                "## [예매 추가]\n" +
                        "이름: **%s**\n" +
                        "연락처: %s\n" +
                        "인원: %d명\n" +
                        "총 금액: %,d원\n" +
                        "마지막 선택한 결제 방식: %s\n" +
                        "시간: %s\n"+
                        "📍예매 누적 인원: %d명\n" +
                        "==========================================",
                requestDto.name(),
                requestDto.phoneNumber(),
                requestDto.headCount(),
                totalPrice,
                requestDto.paymentMethod(),
                booking.formatDateTimeWithDay(now),
                totalBookingCount
        );
        discordWebhookSender.sendBooking(discordMessage);

        // 6. 예매 확인 문자 발송
        String openchatUrl = performance.getOpenchatUrl() != null ? performance.getOpenchatUrl() : "오픈채팅 URL 미등록";

        adminMessageService.sendBookingConfirmationMessage(
                requestDto.phoneNumber(),
                requestDto.name(),
                requestDto.headCount(),
                totalPrice,
                openchatUrl
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PreBookingInfoResponse getPreBookingInfo(Long performanceId) {
        // 1. 공연 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);

        // 2. 오픈채팅방 URL 존재 여부 확인
        String openchatUrl = performance.getOpenchatUrl();
        if (openchatUrl == null || openchatUrl.trim().isEmpty()) {
            throw new OpenChatUrlNotFoundException();
        }

        // TODO: 추후 리팩토링 시 DB에 저장된 값으로 수정 필요
        String entryStartTime = "16:30";

        // 3. 사전 예매 관련 정보 응답 반환
        return new PreBookingInfoResponse(
                openchatUrl,
                entryStartTime,
                performance.getPreSaleEndTime() != null ? performance.getPreSaleEndTime().toString() : "",
                performance.getPreSaleFee() != null ? performance.getPreSaleFee().toString() : "",
                performance.getOnSiteFee() != null ? performance.getOnSiteFee().toString() : "",
                performance.getBankName() != null ? performance.getBankName() : "",
                performance.getBankAccount() != null ? performance.getBankAccount() : "",
                performance.getAccountHolder() != null ? performance.getAccountHolder() : "",
                performance.getKakaopayUrl() != null ? performance.getKakaopayUrl() : "",
                performance.getNaverpayUrl() != null ? performance.getNaverpayUrl() : ""
        );
    }

    // 예매 수정
    @Override
    @Transactional
    public void updateBooking(Long bookingId, BookingUpdateRequestDto requestDto) {
        // 1. 예매 조회
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        // 기존 값 (로그용)
        String oldName = booking.getName();
        String oldPhone = booking.getPhoneNumber();
        int oldHeadCount = booking.getHeadCount();

        // 변경 적용
        booking.setName(requestDto.name());
        booking.setPhoneNumber(requestDto.phoneNumber());
        booking.setHeadCount(requestDto.headCount());

        // 변경 후 누적 인원
        Integer totalBookingCount = bookingRepository.sumHeadCountByPerformance(booking.getPerformance());
        LocalDateTime now = LocalDateTime.now();

        // 디스코드 알림 메시지(관리자)
        String discordMessage = String.format(
                "[예매 수정]\n" +
                        "예매 ID: %d\n\n" +
                        "[변경 전]\n" +
                        "- 이름: %s\n" +
                        "- 연락처: %s\n" +
                        "- 인원: %d명\n\n" +
                        "[변경 후]\n" +
                        "- 이름: %s\n" +
                        "- 연락처: %s\n" +
                        "- 인원: %d명\n\n" +
                        "시간: %s\n" +
                        "📍수정 후 예매 누적 인원: %d명\n" +
                        "=====================",
                bookingId,
                oldName, oldPhone, oldHeadCount,
                requestDto.name(), requestDto.phoneNumber(), requestDto.headCount(),
                booking.formatDateTimeWithDay(now),
                totalBookingCount
        );

        // 문자(예매자)
        adminMessageService.sendSimpleAdminMessage(
                requestDto.phoneNumber(),
                String.format("[들불] 예매 수정\n%s: %d→%d명",
                        requestDto.name(), oldHeadCount, requestDto.headCount())
        );


        discordWebhookSender.sendBooking(discordMessage);
    }


    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        // 1. 예매 조회
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        // 로그용 백업
        String name = booking.getName();
        String phone = booking.getPhoneNumber();
        int headCount = booking.getHeadCount();

        // 삭제
        bookingRepository.delete(booking);

        // 삭제 후 누적 인원
        int totalBookingCount = bookingRepository.sumHeadCountByPerformance(booking.getPerformance());
        LocalDateTime now = LocalDateTime.now();

        // 디스코드 메시지(관리자)
        String discordMessage = String.format(
                "[예매 삭제]\n" +
                        "예매 ID: %d\n" +
                        "이름: %s\n" +
                        "연락처: %s\n" +
                        "인원: %d명\n\n" +
                        "시간: %s\n" +
                        "📍삭제 후 예매 누적 인원: %d명\n" +
                        "=====================",
                bookingId,
                name, phone, headCount,
                booking.formatDateTimeWithDay(now),
                totalBookingCount
        );

        // 문자(예매자)
        adminMessageService.sendSimpleAdminMessage(
                phone,
                String.format("[들불] 예매 삭제\n%s, %d명", name, headCount)
        );

        discordWebhookSender.sendBooking(discordMessage);
    }

}
