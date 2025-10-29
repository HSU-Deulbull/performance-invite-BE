package com.deulbull.performance.domain.admin.entity;

import com.deulbull.performance.domain.admin.entity.enums.AdminRole;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AdminRole role; // 관리자 권한

    private String password;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;
}
