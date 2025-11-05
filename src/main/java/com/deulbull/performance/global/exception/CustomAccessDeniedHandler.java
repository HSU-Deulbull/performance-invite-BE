package com.deulbull.performance.global.exception;

import com.deulbull.performance.global.jwt.exception.JwtErrorCode;
import com.deulbull.performance.global.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증된 사용자가 권한이 없는 경우 사용되는 핸들러 (403)
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse<?> errorResponse = ErrorResponse.of(JwtErrorCode.JWT_403_UNAUTHORIZED);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));

    }
}