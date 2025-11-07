package com.deulbull.performance.domain.admin.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminLoginRequestDto {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
