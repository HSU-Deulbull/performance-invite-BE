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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessageServiceImpl implements AdminMessageService {
    private final AdminRepository adminRepository;
    private final BookingRepository bookingRepository;

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

                System.out.println("문자 발송 실패 (" + booking.getPerformance() + "): " + e.getMessage());
                failCount++;
            }
        }

        // 테스트
        System.out.println("[문자 발송 결과] adminId=" + adminId
                + ", performanceId=" + performance.getId()
                + ", success=" + successCount
                + ", fail=" + failCount);
    }

    // 사전예매 확인 문자 발송
    @Override
    public void sendBookingConfirmationMessage(String phoneNumber, String name, int headCount, int totalPrice, String openchatUrl) {
        try {
            DefaultMessageService smsService =
                    NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

            Message message = new Message();
            message.setFrom(sender);
            message.setTo(phoneNumber);
//            String webUrl = "https://deulbull.netlify.app/";
            // 문자 내용 작성
//            String messageText = String.format(
//                    "[들불] Ready to Fire 정기공연 사전예매 완료 안내\n\n" +
//                    "예매자: %s님\n" +
//                    "인원: %d명\n" +
//                    "총 금액: %,d원\n\n" +
//
//                    "[공연 정보]\n" +
//                    "- 날짜: 12/21(토) 17:00 공연\n" +
//                    "입장: 16:30부터 관객 입장 시작\n" +
//                    "- 장소: 홍대 프리버드 리부트\n\n" +
//
//                    "문의 및 공연 정보:\n%s\n\n" +
//                    "Website: %s\n"+
//
//                    "예매해주셔서 감사합니다.\n" +
//                    "공연 당일에 뵙겠습니다!\n\n",
//                    name, headCount, totalPrice, openchatUrl, webUrl
//            );

            String messageText = String.format(
                    "[들불] %s님 %s명 예매완료\n"+
                    "일시: 12/21(일) 17시\n"+
                    "문의: %s"
            , name, headCount, openchatUrl );

            message.setText(messageText);

            smsService.sendOne(new SingleMessageSendingRequest(message));

            System.out.println("[예매 확인 문자 발송 성공] to=" + phoneNumber + ", name=" + name);
        } catch (Exception e) {
            System.out.println("[예매 확인 문자 발송 실패] to=" + phoneNumber + ", error=" + e.getMessage());
            // 문자 발송 실패해도 예매는 정상적으로 처리되도록 예외를 던지지 않음
        }
    }

    // 간단 문자 발송
    @Override
    public void sendSimpleAdminMessage(String phoneNumber, String messageText) {
        try {
            DefaultMessageService smsService =
                    NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");

            Message message = new Message();
            message.setFrom(sender);
            message.setTo(phoneNumber);
            message.setText(messageText);

            smsService.sendOne(new SingleMessageSendingRequest(message));

            System.out.println("[예매 수정/삭제 문자 발송 성공] to=" + phoneNumber);
        } catch (Exception e) {
            System.out.println("[예매 수정/삭제 문자 발송 실패] to=" + phoneNumber + ", error=" + e.getMessage());
            // 문자 발송 실패해도 예매는 정상적으로 처리되도록 예외를 던지지 않음
        }
    }

}
