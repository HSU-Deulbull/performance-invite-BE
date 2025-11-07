package com.deulbull.performance.domain.admin.web.controller;

import com.deulbull.performance.domain.admin.service.AdminService;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/auth/login")
    public SuccessResponse<AdminLoginResponseDto> login(@RequestBody @Valid AdminLoginRequestDto adminLoginRequestDto) {
        AdminLoginResponseDto data = adminService.login(adminLoginRequestDto);
        return SuccessResponse.created(data);
    }
}
