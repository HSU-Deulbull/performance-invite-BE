package com.deulbull.performance.domain.song.service;

import com.deulbull.performance.domain.song.entity.Song;
import com.deulbull.performance.domain.song.repository.SongRepository;
import com.deulbull.performance.domain.song.web.dto.SongCreateRequestDto;
import com.deulbull.performance.domain.song.web.dto.SongCreateResponseDto;
import com.deulbull.performance.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public SongCreateResponseDto createSongs(SongCreateRequestDto requestDto, List<MultipartFile> albumImages) {
        List<SongCreateResponseDto.SongDetail> createdSongs = new ArrayList<>();
        List<SongCreateRequestDto.SongInfo> songInfos = requestDto.songs();

        for (int i = 0; i < songInfos.size(); i++) {
            SongCreateRequestDto.SongInfo songInfo = songInfos.get(i);

            // 1. Song 엔티티 생성
            Song song = Song.builder()
                    .artist(songInfo.artist())
                    .title(songInfo.title())
                    .album(songInfo.album())
                    .releaseDate(parseDate(songInfo.releaseDate()))
                    .genre(songInfo.genre())
                    .youtubeUrl(songInfo.youtubeUrl())
                    .lyrics(songInfo.lyrics())
                    .albumImgUrl(null) // 초기값 null
                    .build();

            songRepository.save(song);

            // 2. 앨범 이미지가 있으면 S3에 업로드
            String albumImgUrl = null;
            if (albumImages != null && i < albumImages.size()) {
                MultipartFile albumImage = albumImages.get(i);
                if (albumImage != null && !albumImage.isEmpty()) {
                    albumImgUrl = s3Uploader.upload(
                            albumImage,
                            "song/" + song.getId() + "/album-img"
                    );
                    song.setAlbumImgUrl(albumImgUrl);
                }
            }

            // 3. 응답 DTO 생성
            createdSongs.add(new SongCreateResponseDto.SongDetail(
                    song.getId(),
                    song.getArtist(),
                    song.getTitle(),
                    song.getAlbum(),
                    song.getReleaseDate() != null ? song.getReleaseDate().toString() : null,
                    song.getGenre(),
                    song.getYoutubeUrl(),
                    song.getAlbumImgUrl(),
                    song.getLyrics()
            ));
        }

        return new SongCreateResponseDto(createdSongs.size(), createdSongs);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            return null;
        }
    }
}
