package com.deulbull.performance.domain.song.web.controller;

import com.deulbull.performance.domain.song.service.SongService;
import com.deulbull.performance.domain.song.web.dto.SongCreateRequestDto;
import com.deulbull.performance.domain.song.web.dto.SongCreateResponseDto;
import com.deulbull.performance.domain.song.web.dto.SongPersonConnectRequestDto;
import com.deulbull.performance.domain.song.web.dto.SongPersonConnectResponseDto;
import com.deulbull.performance.global.response.SuccessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public SuccessResponse<SongCreateResponseDto> createSongs(
            @RequestPart("data") String requestData,
            @RequestPart(value = "albumImages", required = false) List<MultipartFile> albumImages
    ) {
        try {
            SongCreateRequestDto requestDto = objectMapper.readValue(requestData, SongCreateRequestDto.class);
            return SuccessResponse.ok(songService.createSongs(requestDto, albumImages));
        } catch (Exception e) {
            throw new RuntimeException("Invalid request data: " + e.getMessage(), e);
        }
    }

    @PostMapping("/connect-person")
    public SuccessResponse<SongPersonConnectResponseDto> connectSongToPerson(
            @Valid @RequestBody SongPersonConnectRequestDto requestDto
    ) {
        return SuccessResponse.ok(songService.connectSongToPerson(requestDto));
    }
}
