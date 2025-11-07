package com.deulbull.performance.global.exception;

import com.deulbull.performance.global.jwt.exception.JwtErrorCode;
import com.deulbull.performance.global.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 핸들러 (401)
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // JwtTokenFilter에서 설정한 예외 정보 확인
        Exception exception = (Exception) request.getAttribute("exception");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse<?> errorResponse;

        // 토큰 만료 예외인 경우
        if (exception instanceof ExpiredJwtException) {
            errorResponse = ErrorResponse.of(JwtErrorCode.JWT_401_EXPIRED);
        } else {
            // 그 외의 인증 실패 (토큰 없음, 유효하지 않음 등)
            errorResponse = ErrorResponse.of(JwtErrorCode.JWT_401_EXPIRED);
        }

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
