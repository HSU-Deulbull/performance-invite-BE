package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;

public interface AdminService {
    // 로그인
    public AdminLoginResponseDto login(AdminLoginRequestDto adminLoginRequestDto);
}
