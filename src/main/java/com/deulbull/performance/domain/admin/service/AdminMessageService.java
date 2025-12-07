package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.web.dto.AdminMessageRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminMessageTargetCountResponseDto;
import jakarta.validation.Valid;

public interface AdminMessageService {

    // 문자 발송 대상 인원 수 조회
    AdminMessageTargetCountResponseDto getMessageTargetCount(Long adminId);

    // 단체 문자 발송
    void sendBulkMessage(Long adminId, @Valid AdminMessageRequestDto adminMessageRequestDto);

    // 사전예매 확인 문자 발송
    void sendBookingConfirmationMessage(String phoneNumber, String name, int headCount, int totalPrice, String openchatUrl);

    // 간단 문자 발송
    void sendSimpleAdminMessage(String content);

}
