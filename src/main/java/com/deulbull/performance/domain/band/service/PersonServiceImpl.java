package com.deulbull.performance.domain.band.service;

import com.deulbull.performance.domain.band.entity.Person;
import com.deulbull.performance.domain.band.repository.PersonRepository;
import com.deulbull.performance.domain.band.web.dto.PersonRequestDto;
import com.deulbull.performance.domain.band.web.dto.PersonResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public List<PersonResponseDto> createPersons(List<PersonRequestDto> requestDtos) {
        // 여러 명의 Person 생성
        List<Person> persons = requestDtos.stream()
                .map(dto -> Person.builder()
                        .studentId(dto.studentId())
                        .name(dto.name())
                        .instagramId(dto.instagramId())
                        .build())
                .toList();

        // 일괄 저장
        List<Person> savedPersons = personRepository.saveAll(persons);

        // 응답 반환
        return savedPersons.stream()
                .map(PersonResponseDto::from)
                .toList();
    }
}
