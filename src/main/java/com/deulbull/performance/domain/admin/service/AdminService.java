package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;
import com.deulbull.performance.domain.admin.web.dto.AdminSignupRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminSignupResponseDto;
import jakarta.validation.Valid;

public interface AdminService {
    // 로그인
    public AdminLoginResponseDto login(AdminLoginRequestDto adminLoginRequestDto);

    // 회원가입
    AdminSignupResponseDto signup(@Valid AdminSignupRequestDto adminSignupRequestDto);
}
