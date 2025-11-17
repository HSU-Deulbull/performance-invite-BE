package com.deulbull.performance.domain.band.web.dto;

import com.deulbull.performance.domain.band.entity.Person;
import lombok.Builder;

@Builder
public record PersonResponseDto(
        Long id,
        String studentId,
        String name,
        String phoneNumber,
        String instagramId,
        String createdAt,
        String updatedAt
) {
    public static PersonResponseDto from(Person person) {
        return PersonResponseDto.builder()
                .id(person.getId())
                .studentId(person.getStudentId())
                .name(person.getName())
                .phoneNumber(person.getPhoneNumber())
                .instagramId(person.getInstagramId())
                .createdAt(person.formatDateTimeWithDay(person.getCreatedAt()))
                .updatedAt(person.formatDateTimeWithDay(person.getUpdatedAt()))
                .build();
    }
}
