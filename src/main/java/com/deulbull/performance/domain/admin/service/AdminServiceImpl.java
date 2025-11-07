package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.exception.AdminInvalidPasswordException;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    @Override
    public AdminLoginResponseDto login(AdminLoginRequestDto adminLoginRequestDto) {
        String inputPassword = adminLoginRequestDto.getPassword();

        // 1. 모든 admin 조회 후 비밀번호 검증
        Admin admin = adminRepository.findAll()
                .stream()
                .filter(a -> passwordEncoder.matches(inputPassword, a.getPassword()))
                .findFirst()
                .orElseThrow(AdminInvalidPasswordException::new);

        // 2. Band 정보 다시 조회
        admin = adminRepository.findByIdWithBand(admin.getId())
                .orElseThrow(AdminInvalidPasswordException::new);

        Performance performance = admin.getPerformance();
        String token = jwtTokenProvider.createToken(admin);

        return new AdminLoginResponseDto(
                token,
                performance.getDescription(),
                admin.getBand().getBandName(),
                admin.getRole().toString()
        );
    }
}
