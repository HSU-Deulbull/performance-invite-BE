package com.deulbull.performance.domain.admin.web.controller;

import com.deulbull.performance.domain.admin.service.AdminService;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;
import com.deulbull.performance.domain.admin.web.dto.AdminSignupRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminSignupResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    // 로그인
    @PostMapping("/auth/login")
    public SuccessResponse<AdminLoginResponseDto> login(@RequestBody @Valid AdminLoginRequestDto adminLoginRequestDto) {
        AdminLoginResponseDto data = adminService.login(adminLoginRequestDto);
        return SuccessResponse.ok(data);
    }

    // 회원가입
    @PostMapping("/auth/signup")
    public SuccessResponse<AdminSignupResponseDto> signup(
            @RequestBody @Valid AdminSignupRequestDto adminSignupRequestDto) {
        AdminSignupResponseDto data = adminService.signup(adminSignupRequestDto);
        return SuccessResponse.created(data);
    }
}
