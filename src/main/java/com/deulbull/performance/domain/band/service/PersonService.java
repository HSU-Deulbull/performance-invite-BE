package com.deulbull.performance.domain.band.service;

import com.deulbull.performance.domain.band.web.dto.PersonRequestDto;
import com.deulbull.performance.domain.band.web.dto.PersonResponseDto;

import java.util.List;

public interface PersonService {
    // 여러 명의 Person 생성
    List<PersonResponseDto> createPersons(List<PersonRequestDto> requestDtos);
}
