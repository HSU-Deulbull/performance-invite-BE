package com.deulbull.performance.domain.band.service;

import com.deulbull.performance.domain.band.web.dto.BandRequestDto;
import com.deulbull.performance.domain.band.web.dto.BandResponseDto;

public interface BandService {
    // 밴드 생성
    BandResponseDto createBand(BandRequestDto requestDto);
}
