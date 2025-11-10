package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.exception.AdminNotFoundException;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageTargetCountResponseDto;
import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.performance.entity.Performance;
import net.nurigo.sdk.message.model.Message;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessageServiceImpl implements AdminMessageService {
    private final AdminRepository adminRepository;
    private final BookingRepository bookingRepository;
    private final Logger logger;


    @Value("${sms.api.key}")
    private String apiKey;
    @Value("${sms.api.secret}")
    private String apiSecret;
    @Value("${sms.from}")
    private String sender;

    // 문자 발송 대상 인원 수 조회
    @Override
    public AdminMessageTargetCountResponseDto getMessageTargetCount(Long adminId) {
        // 404: 해당 ID의 관리자 없음
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        Performance performance = admin.getPerformance();

        List<Booking> bookings = bookingRepository.findAllByPerformanceId(performance.getId());

        // 예매자 수
        int smsTargetCount = bookings.size();

        // 총 예매 인원 수
        int totalBookingCount = bookings.stream()
                .mapToInt(Booking::getHeadCount)
                .sum();

        // 반환
        return new AdminMessageTargetCountResponseDto(
                smsTargetCount,
                totalBookingCount
        );
    }

    // 단체 문자 발송
    @Override
    public void sendBulkMessage(Long adminId, AdminMessageRequestDto adminMessageRequestDto) {
        // 404: 관리자 없음
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);
        Performance performance = admin.getPerformance();

        // 공연별 예매자 조회
        List<Booking> bookings = bookingRepository.findAllByPerformanceId(performance.getId());

        DefaultMessageService smsService =
                NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

        int successCount = 0;
        int failCount = 0;

        // 문자 발송
        for (Booking booking : bookings) {
            try {
                Message message = new Message();
                message.setFrom(sender);
                message.setTo(booking.getPhoneNumber());
                message.setText(adminMessageRequestDto.getMessage());

                if ("LMS".equalsIgnoreCase(adminMessageRequestDto.getType())) {
                    message.setSubject(adminMessageRequestDto.getTitle());
                }

                smsService.sendOne(new SingleMessageSendingRequest(message));
                successCount++;
            } catch (Exception e) {
                logger.error("문자 발송 실패 ({}): {}", booking.getPerformance(), e.getMessage());
                failCount++;
            }
        }

        // 로그 남기기
        logger.info("[문자 발송 결과] adminId={}, performanceId={}, success={}, fail={}",
                adminId, performance.getId(), successCount, failCount);
    }


}
