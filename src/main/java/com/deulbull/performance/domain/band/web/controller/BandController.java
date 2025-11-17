package com.deulbull.performance.domain.band.web.controller;

import com.deulbull.performance.domain.band.service.BandService;
import com.deulbull.performance.domain.band.web.dto.BandRequestDto;
import com.deulbull.performance.domain.band.web.dto.BandResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bands")
@RequiredArgsConstructor
public class BandController {

    private final BandService bandService;

    // 밴드 생성 API
    @PostMapping
    public SuccessResponse<BandResponseDto> createBand(
            @Valid @RequestBody BandRequestDto requestDto
    ) {
        BandResponseDto response = bandService.createBand(requestDto);
        return SuccessResponse.created(response);
    }
}
