package com.deulbull.performance.domain.booking.web.controller;

import com.deulbull.performance.domain.booking.entity.Booking;
import com.deulbull.performance.domain.booking.repository.BookingRepository;
import com.deulbull.performance.domain.performance.entity.Performance;
import com.deulbull.performance.domain.performance.repository.PerformanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Performance testPerformance;

    @BeforeEach
    void setUp() {
        // 테스트용 공연 데이터 생성
        testPerformance = Performance.builder()
                .websiteName("테스트 웹사이트")
                .websiteDescription("테스트 설명")
                .title("테스트 공연")
                .subtitle("테스트 부제")
                .description("테스트 공연 설명")
                .location("서울 강남구")
                .venue("테스트 공연장")
                .dateTime(LocalDateTime.now().plusDays(7))
                .preSaleFee(10000)
                .onSiteFee(15000)
                .preSaleEndTime(LocalDateTime.now().plusDays(5)) // 5일 후 예매 마감
                .posterFrontUrl("http://example.com/poster-front.jpg")
                .posterBackUrl("http://example.com/poster-back.jpg")
                .openchatUrl("http://example.com/openchat")
                .build();

        testPerformance = performanceRepository.save(testPerformance);
    }

    @Test
    @DisplayName("정상적인 예매 생성 - 201 Created")
    void createBooking_Success() throws Exception {
        // given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "이동건");
        requestBody.put("phoneNumber", "010-1234-5678");
        requestBody.put("headCount", 2);

        // when & then
        mockMvc.perform(post("/performances/{performanceId}/bookings", testPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("CREATED_201"))
                .andExpect(jsonPath("$.message").value("예매 성공하였습니다."))
                .andExpect(jsonPath("$.httpStatus").value(201));

        // 데이터베이스 확인
        Booking savedBooking = bookingRepository.findAll().get(0);
        assertThat(savedBooking.getName()).isEqualTo("이동건");
        assertThat(savedBooking.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(savedBooking.getHeadCount()).isEqualTo(2);
        assertThat(savedBooking.getPerformance().getId()).isEqualTo(testPerformance.getId());
    }

    @Test
    @DisplayName("존재하지 않는 공연 ID - 404 Not Found")
    void createBooking_PerformanceNotFound() throws Exception {
        // given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "이동건");
        requestBody.put("phoneNumber", "010-1234-5678");
        requestBody.put("headCount", 2);

        Long nonExistentPerformanceId = 99999L;

        // when & then
        mockMvc.perform(post("/performances/{performanceId}/bookings", nonExistentPerformanceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("PERFORMANCE_404_NOT_FOUND"));
    }

    @Test
    @DisplayName("예매 마감 기한이 지난 경우 - 400 Bad Request")
    void createBooking_DeadlinePassed() throws Exception {
        // given - 예매 마감 기한이 지난 공연 생성
        Performance expiredPerformance = Performance.builder()
                .websiteName("마감된 공연")
                .title("마감된 공연")
                .subtitle("테스트")
                .description("테스트")
                .location("서울")
                .venue("테스트 공연장")
                .dateTime(LocalDateTime.now().plusDays(7))
                .preSaleFee(10000)
                .onSiteFee(15000)
                .preSaleEndTime(LocalDateTime.now().minusDays(1)) // 어제 예매 마감
                .posterFrontUrl("http://example.com/poster-front.jpg")
                .posterBackUrl("http://example.com/poster-back.jpg")
                .build();
        expiredPerformance = performanceRepository.save(expiredPerformance);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "이동건");
        requestBody.put("phoneNumber", "010-1234-5678");
        requestBody.put("headCount", 2);

        // when & then
        mockMvc.perform(post("/performances/{performanceId}/bookings", expiredPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("예매가 마감되었습니다."));
    }

    @Test
    @DisplayName("잘못된 요청 - 이름 누락 - 400 Bad Request")
    void createBooking_InvalidRequest_MissingName() throws Exception {
        // given
        Map<String, Object> requestBody = new HashMap<>();
        // name 누락
        requestBody.put("phoneNumber", "010-1234-5678");
        requestBody.put("headCount", 2);

        // when & then
        mockMvc.perform(post("/performances/{performanceId}/bookings", testPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 요청 - 전화번호 형식 오류 - 400 Bad Request")
    void createBooking_InvalidRequest_InvalidPhoneNumber() throws Exception {
        // given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "이동건");
        requestBody.put("phoneNumber", "01012345678"); // 하이픈 없음
        requestBody.put("headCount", 2);

        // when & then
        mockMvc.perform(post("/performances/{performanceId}/bookings", testPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 요청 - 인원수 0 이하 - 400 Bad Request")
    void createBooking_InvalidRequest_InvalidHeadCount() throws Exception {
        // given
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "이동건");
        requestBody.put("phoneNumber", "010-1234-5678");
        requestBody.put("headCount", 0); // 0명

        // when & then
        mockMvc.perform(post("/performances/{performanceId}/bookings", testPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("여러 명 예매 가능 - 인원 제한 없음")
    void createBooking_MultipleBookings() throws Exception {
        // given - 첫 번째 예매
        Map<String, Object> requestBody1 = new HashMap<>();
        requestBody1.put("name", "홍길동");
        requestBody1.put("phoneNumber", "010-1111-2222");
        requestBody1.put("headCount", 5);

        mockMvc.perform(post("/performances/{performanceId}/bookings", testPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody1)))
                .andExpect(status().isCreated());

        // given - 두 번째 예매
        Map<String, Object> requestBody2 = new HashMap<>();
        requestBody2.put("name", "김철수");
        requestBody2.put("phoneNumber", "010-3333-4444");
        requestBody2.put("headCount", 10);

        // when & then - 인원 제한이 없으므로 두 번째 예매도 성공해야 함
        mockMvc.perform(post("/performances/{performanceId}/bookings", testPerformance.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody2)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.message").value("예매 성공하였습니다."));

        // 데이터베이스 확인 - 총 2개의 예매가 저장되어야 함
        assertThat(bookingRepository.count()).isEqualTo(2);
    }
}
