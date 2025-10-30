package com.deulbull.performance.domain.song.entity;

import com.deulbull.performance.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private String album;
    private LocalDate releaseDate;
    private String genre;
    private String youtubeUrl; // 유튜브 링크
    private String albumImgUrl; // 앨범 이미지 링크

    @Column(columnDefinition = "TEXT")
    private String lyrics; // 가사
}
