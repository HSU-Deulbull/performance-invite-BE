package com.deulbull.performance.domain.band.entity;

import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Band extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String logoUrl;
    @Column(nullable = false)
    private String name; // 필수
    private String instagramUrl;
    private String youtubeUrl;
}
