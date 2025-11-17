package com.deulbull.performance.domain.band.web.controller;

import com.deulbull.performance.domain.band.service.PersonService;
import com.deulbull.performance.domain.band.web.dto.PersonRequestDto;
import com.deulbull.performance.domain.band.web.dto.PersonResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    // 여러 명의 Person 생성 API
    @PostMapping
    public SuccessResponse<List<PersonResponseDto>> createPersons(
            @Valid @RequestBody List<PersonRequestDto> requestDtos
    ) {
        List<PersonResponseDto> responses = personService.createPersons(requestDtos);
        return SuccessResponse.created(responses);
    }
}
