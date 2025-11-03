package com.deulbull.performance.domain.performanceSongs.service;

import com.deulbull.performance.domain.band.entity.BandSession;
import com.deulbull.performance.domain.band.repository.BandSessionRepository;
import com.deulbull.performance.domain.performanceSongs.entity.PerformanceSong;
import com.deulbull.performance.domain.performanceSongs.exception.PerformanceSongsNotFoundException;
import com.deulbull.performance.domain.performanceSongs.repository.PerformanceSongsRepository;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsDetailResponseDto;
import com.deulbull.performance.domain.song.entity.Song;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceSongsServiceImpl implements PerformanceSongsService {
    private final PerformanceSongsRepository performanceSongsRepository;
    private final BandSessionRepository bandSessionRepository;

    @Override
    public PerformanceSongsDetailResponseDto getPerformanceSongsDetail(Long performanceSongId) {
        PerformanceSong ps = performanceSongsRepository.findWithSongById(performanceSongId)
                .orElseThrow(PerformanceSongsNotFoundException::new);

        Song s = ps.getSong();
        String releaseDateStr = (s.getReleaseDate() != null)
                ? s.getReleaseDate().format(DateTimeFormatter.ISO_DATE)
                : null;
        // 트랙 생성 (곡 정보)
        PerformanceSongsDetailResponseDto.Track track =
                new PerformanceSongsDetailResponseDto.Track(
                        ps.getLikes(),
                        s.getTitle(),
                        s.getArtist(),
                        s.getAlbum(),
                        s.getGenre(),
                        releaseDateStr,
                        s.getYoutubeUrl(),
                        s.getLyrics(),
                        s.getAlbumImgUrl()
                );

        // BandSession + Person 로드
        List<BandSession> sessions =
                bandSessionRepository.findAllByPerformanceSongIdWithPerson(performanceSongId);

        // 팀 생성 (세션 정보 + 이름 + 인스타 아이디)
        List<PerformanceSongsDetailResponseDto.Team> team = sessions.stream()
                .map(bs -> new PerformanceSongsDetailResponseDto.Team(
                        bs.getSessionType(),
                        bs.getPerson() != null ? bs.getPerson().getName() : null,
                        bs.getPerson() != null ? bs.getPerson().getInstagramId() : null
                ))
                .toList();

        return new PerformanceSongsDetailResponseDto(track, team);
    }
}
