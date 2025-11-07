package com.deulbull.performance.global.security;

import com.deulbull.performance.domain.admin.entity.Admin;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 인증된 사용자 정보(Admin)를 담기 위한 UserDetails 구현체
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Admin admin;

    public CustomUserDetails(Admin admin) {
        this.admin = admin;
    }

    public Long getAdminId() {
        return admin.getId();
    }

    // 사용자 권한 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));
    }

    @Override
    public String getPassword() {
        return admin.getPassword();
    }

    // 사용자 식별을 위한 정보 (username이 없으므로 id 사용)
    @Override
    public String getUsername() {
        return admin.getId().toString();
    }
}