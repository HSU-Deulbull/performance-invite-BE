package com.deulbull.performance.domain.admin.web.dto;

import java.util.List;

public record BookingListResponseDto(
        String performanceTitle,
        Integer currentTotalHeadCount,
        Long totalBookingCount,
        PageInfo pageInfo,
        List<BookingDto> bookings
) {
    public record PageInfo(
            int currentPage,
            int totalPages,
            long totalElements,
            int size
    ) {}

    public record BookingDto(
            Long bookingId,
            String name,
            String phoneNumber,
            Integer headCount,
            String bookedAt
    ) {}
}
