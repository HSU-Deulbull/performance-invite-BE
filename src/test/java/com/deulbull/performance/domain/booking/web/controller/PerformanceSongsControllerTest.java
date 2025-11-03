package com.deulbull.performance.domain.booking.web.controller;

import com.deulbull.performance.domain.band.entity.enums.SessionType;
import com.deulbull.performance.domain.performanceSongs.exception.PerformanceSongsNotFoundException;
import com.deulbull.performance.domain.performanceSongs.service.PerformanceSongsService;
import com.deulbull.performance.domain.performanceSongs.web.controller.PerformanceSongsController;
import com.deulbull.performance.domain.performanceSongs.web.dto.PerformanceSongsDetailResponseDto;
import com.deulbull.performance.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PerformanceSongsControllerTest {

    private MockMvc mockMvc;
    private PerformanceSongsService performanceSongsService;

    @BeforeEach
    void setUp() {
        performanceSongsService = Mockito.mock(PerformanceSongsService.class);
        var controller = new PerformanceSongsController(performanceSongsService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    @DisplayName("GET /api/tracks/{id} → 상세 성공(JSON Content-Type 포함)")
    void getTrackDetail_success() throws Exception {
        Long performanceSongId = 101L;

        var track = new PerformanceSongsDetailResponseDto.Track(
                37, "너에게 난, 나에게 넌", "자전거 탄 풍경",
                "Classic", "Ballad", "2001-06-14",
                "https://youtu.be/abcd", "가사 전문 ...",
                "https://img.example/album.jpg"
        );

        var team = List.of(
                new PerformanceSongsDetailResponseDto.Team(SessionType.VOCAL, "강범준", "beom_jun"),
                new PerformanceSongsDetailResponseDto.Team(SessionType.DRUM,  "김주호", "juho_kim")
        );

        var dto = new PerformanceSongsDetailResponseDto(track, team);

        Mockito.when(performanceSongsService.getPerformanceSongsDetail(eq(performanceSongId)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/tracks/{performanceSongId}", performanceSongId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.track.likes").value(37))
                .andExpect(jsonPath("$.team", hasSize(2)));
    }
    @Test
    @DisplayName("GET /api/tracks/{id} → 존재하지 않으면 404 + JSON 에러바디")
    void getTrackDetail_notFound() throws Exception {
        Long performanceSongId = 9999L;

        // ✅ 커스텀 예외를 던지도록 스텁 (메서드명 정확!)
        Mockito.when(performanceSongsService.getPerformanceSongsDetail(eq(performanceSongId)))
                .thenThrow(new PerformanceSongsNotFoundException());

        mockMvc.perform(get("/api/tracks/{performanceSongId}", performanceSongId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PERFORMANCE_SONGS_404_NOT_FOUND"))
                .andExpect(jsonPath("$.httpStatus").value(404))
                .andExpect(jsonPath("$.message").value("해당 ID의 곡을 찾을 수 없습니다."));
    }
}

