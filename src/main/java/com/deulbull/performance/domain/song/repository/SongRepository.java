package com.deulbull.performance.domain.song.repository;

import com.deulbull.performance.domain.song.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    // 곡 제목과 아티스트로 중복 체크
    Optional<Song> findByTitleAndArtist(String title, String artist);
}
