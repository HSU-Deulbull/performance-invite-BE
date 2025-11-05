package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.exception.AdminNotFoundException;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;

    @Override
    public AdminLoginResponseDto login(AdminLoginRequestDto adminLoginRequestDto) {
        String password = adminLoginRequestDto.getPassword();
        Admin admin = adminRepository.findByPassword(password)
                .orElseThrow(AdminNotFoundException::new);

        Performance performance = admin.getPerformance();

        // jwt 토큰 생성
        String token = jwtTokenProvider.createToken(admin);
        return new AdminLoginResponseDto(
                token,
                performance.getDescription(),
                admin.getBand().getBandName(),
                admin.getRole().toString()
        );
    }
}
