package com.deulbull.performance.global.config;

import com.deulbull.performance.global.exception.CustomAccessDeniedHandler;
import com.deulbull.performance.global.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final JwtTokenFilter jwtTokenFilter;

    // 비밀번호 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/login").permitAll()
                        .requestMatchers("/api/admin/**").authenticated() // 관리자만 관리자 페이지 접근 가능
                        .requestMatchers("/api/**").permitAll()  // 다른 API는 모두 접근
                        .anyRequest().permitAll()
                )

                // 4. JWT 필터 등록
                // 스프링 시큐리티에서 제공하는 로그인 처리 필터인 UsernamePasswordAuthenticationFilter 전에 실행되도록 설정
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)

                // 예외처리 관련
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(customAccessDeniedHandler) // JWT_403_UNAUTHORIZED
                );
        ;
        return http.build();
    }
}
