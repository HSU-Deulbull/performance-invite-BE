package com.deulbull.performance.domain.admin.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class AdminMessageRequestDto {
    private String title;
    private String message;
    private String type;
}
