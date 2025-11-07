package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.entity.enums.AdminRole;
import com.deulbull.performance.domain.admin.exception.AdminInvalidPasswordException;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminLoginResponseDto;
import com.deulbull.performance.domain.admin.web.dto.AdminSignupRequestDto;
import com.deulbull.performance.domain.admin.web.dto.AdminSignupResponseDto;
import com.deulbull.performance.domain.band.entity.Band;
import com.deulbull.performance.domain.band.exception.BandNotFoundException;
import com.deulbull.performance.domain.band.repository.BandRepository;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.exception.PerformanceNotFoundException;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.deulbull.performance.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerformanceRepository performanceRepository;
    private final BandRepository bandRepository;

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

        // 3. 반환
        return new AdminLoginResponseDto(
                token,
                performance.getDescription(),
                admin.getBand().getBandName(),
                admin.getRole().toString()
        );
    }

    @Transactional
    @Override
    public AdminSignupResponseDto signup(AdminSignupRequestDto adminSignupRequestDto) {
        String password = adminSignupRequestDto.getPassword();
        AdminRole role = adminSignupRequestDto.getRole();
        Long performanceId = adminSignupRequestDto.getPerformanceId();
        Long bandId = adminSignupRequestDto.getBandId();

        // 1. Performance 조회
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);

        // 2. Band 조회
        Band band = bandRepository.findById(bandId)
                .orElseThrow(BandNotFoundException::new);

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 4. Admin 생성
        Admin admin = Admin.builder()
                .role(role)
                .password(encodedPassword)
                .performance(performance)
                .band(band)
                .build();

        // 5. DB에 저장
        Admin savedAdmin = adminRepository.save(admin);

        // 6. JWT 토큰 생성
        String token = jwtTokenProvider.createToken(savedAdmin);

        // 7. 반환
        return new AdminSignupResponseDto(token);
    }
}
