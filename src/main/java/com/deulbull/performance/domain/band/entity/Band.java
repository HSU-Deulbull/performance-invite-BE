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

    private String bandNameKr; // 밴드 이름 (한글)
    private String bandNameEn; // 밴드 이름 (영문)
    private String bandType; // 밴드 구분 (ex. 정기공연)

    private String logoUrl;
    private String instagramUrl;
    private String youtubeUrl;
    private String goodsUrl; // 굿즈 url
    private String eventNoticeUrl; // 이벤트 안내 url

}
