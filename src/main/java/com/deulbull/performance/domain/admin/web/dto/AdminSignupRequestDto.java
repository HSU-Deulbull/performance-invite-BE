package com.deulbull.performance.domain.admin.web.dto;

import com.deulbull.performance.domain.admin.entity.enums.AdminRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSignupRequestDto {

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @NotNull(message = "역할은 필수입니다")
    private AdminRole role;

    @NotNull(message = "공연 ID는 필수입니다")
    private Long performanceId;

    @NotNull(message = "밴드 ID는 필수입니다")
    private Long bandId;
}