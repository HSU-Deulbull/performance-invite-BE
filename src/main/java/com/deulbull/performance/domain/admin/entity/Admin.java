package com.deulbull.performance.domain.admin.entity;

import com.deulbull.performance.domain.admin.entity.enums.AdminRole;
import com.deulbull.performance.domain.band.entity.Band;
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

    @ManyToOne(fetch = FetchType.EAGER) // 항상 같이 로드
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY) // 필요시에만 JOIN FETCH 예정
    @JoinColumn(name = "band_id")
    private Band band;
}
