package com.deulbull.performance.domain.admin.service;

import com.deulbull.performance.domain.admin.entity.Admin;
import com.deulbull.performance.domain.admin.entity.enums.AdminRole;
import com.deulbull.performance.domain.admin.exception.AdminInvalidPasswordException;
import com.deulbull.performance.domain.admin.exception.AdminNotFoundException;
import com.deulbull.performance.domain.admin.repository.AdminRepository;
import com.deulbull.performance.domain.admin.web.dto.*;
import com.deulbull.performance.domain.admin.web.dto.BookingListResponseDto.BookingDto;
import com.deulbull.performance.domain.admin.web.dto.BookingListResponseDto.PageInfo;
import com.deulbull.performance.domain.band.entity.Band;
import com.deulbull.performance.domain.band.exception.BandNotFoundException;
import com.deulbull.performance.domain.band.repository.BandRepository;
import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.exception.PerformanceNotFoundException;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.deulbull.performance.global.jwt.JwtTokenProvider;
import com.deulbull.performance.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final PerformanceRepository performanceRepository;
    private final BandRepository bandRepository;
    private final BookingRepository bookingRepository;

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

    // 예매 현황 전체 조회
    @Override
    @Transactional(readOnly = true)
    public BookingListResponseDto getBookingList(Long adminId, int page, int size, String search) {
        // 1. Admin 조회 (Performance 정보 포함 - EAGER로 자동 로드됨)
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(AdminNotFoundException::new);

        Performance performance = admin.getPerformance();

        // 2. 페이징 설정 (createdAt 기준 내림차순)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 3. 예매 목록 조회 (검색어가 있으면 이름 검색, 없으면 전체 조회)
        Page<Booking> bookingPage;
        Integer totalHeadCount;
        long totalBookingCount;

        boolean hasSearchQuery = search != null && !search.trim().isEmpty();

        if (hasSearchQuery) {
            // 검색어가 있는 경우
            String trimmedSearch = search.trim();
            bookingPage = bookingRepository.findByPerformanceAndNameContaining(performance, trimmedSearch, pageable);
            totalHeadCount = bookingRepository.sumHeadCountByPerformanceAndNameContaining(performance, trimmedSearch);
            totalBookingCount = bookingRepository.countByPerformanceAndNameContaining(performance, trimmedSearch);
        } else {
            // 검색어가 없는 경우 (전체 조회)
            bookingPage = bookingRepository.findByPerformance(performance, pageable);
            totalHeadCount = bookingRepository.sumHeadCountByPerformance(performance);
            totalBookingCount = bookingRepository.countByPerformance(performance);
        }

        // 4. DTO 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm");
        List<BookingDto> bookingDtos = bookingPage.getContent().stream()
                .map(booking -> new BookingDto(
                        booking.getId(),
                        booking.getName(),
                        booking.getPhoneNumber(),
                        booking.getHeadCount(),
                        booking.getCreatedAt().format(formatter)
                ))
                .toList();

        PageInfo pageInfo = new PageInfo(
                bookingPage.getNumber(),
                bookingPage.getTotalPages(),
                bookingPage.getTotalElements(),
                bookingPage.getSize()
        );

        return new BookingListResponseDto(
                performance.getTitle(),
                totalHeadCount,
                totalBookingCount,
                pageInfo,
                bookingDtos
        );
    }
}
