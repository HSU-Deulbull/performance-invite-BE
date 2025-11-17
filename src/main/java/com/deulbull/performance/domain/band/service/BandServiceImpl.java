package com.deulbull.performance.domain.band.service;

import com.deulbull.performance.domain.band.entity.Band;
import com.deulbull.performance.domain.band.repository.BandRepository;
import com.deulbull.performance.domain.band.web.dto.BandRequestDto;
import com.deulbull.performance.domain.band.web.dto.BandResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService {
    private final BandRepository bandRepository;

    @Override
    @Transactional
    public BandResponseDto createBand(BandRequestDto requestDto) {
        // 밴드 생성
        Band band = Band.builder()
                .bandName(requestDto.bandName())
                .logoUrl(null) // TODO: 로고 필요시 S3 업로드 로직 추가
                .build();

        bandRepository.save(band);

        // 응답 반환
        return BandResponseDto.from(band);
    }
}
