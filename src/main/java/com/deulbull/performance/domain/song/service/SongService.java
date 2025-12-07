package com.deulbull.performance.domain.song.service;

import com.deulbull.performance.domain.song.web.dto.SongCreateRequestDto;
import com.deulbull.performance.domain.song.web.dto.SongCreateResponseDto;
import com.deulbull.performance.domain.song.web.dto.SongPersonConnectRequestDto;
import com.deulbull.performance.domain.song.web.dto.SongPersonConnectResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SongService {
    SongCreateResponseDto createSongs(SongCreateRequestDto requestDto, List<MultipartFile> albumImages);
    SongPersonConnectResponseDto connectSongToPerson(SongPersonConnectRequestDto requestDto);
}
