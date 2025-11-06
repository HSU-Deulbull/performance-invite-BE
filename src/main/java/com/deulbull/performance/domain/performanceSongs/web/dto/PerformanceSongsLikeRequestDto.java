package com.deulbull.performance.domain.performanceSongs.web.dto;

import jakarta.validation.constraints.NotNull;

public record PerformanceSongsLikeRequestDto (
        @NotNull(message = "잘못된 인자입니다.")
        Boolean liked
){}
