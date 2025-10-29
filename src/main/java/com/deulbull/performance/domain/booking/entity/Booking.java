package com.deulbull.performance.domain.booking.entity;

import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

// 예매
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name; // 예매자 이름
    private String phoneNumber; // 예매자 전화번호
    @NotBlank
    private Integer headCount; // 인원수

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;
}
