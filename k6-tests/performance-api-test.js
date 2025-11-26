import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// 커스텀 메트릭 정의
const errorRate = new Rate('errors');
const performanceDetailTrend = new Trend('performance_detail_duration');
const setlistTrend = new Trend('setlist_duration');
const trackDetailTrend = new Trend('track_detail_duration');

// 테스트 설정
export const options = {
  stages: [
    { duration: '30s', target: 10 },   // Warm-up: 30초 동안 10명까지 증가
    { duration: '1m', target: 50 },    // Ramp-up: 1분 동안 50명까지 증가
    { duration: '3m', target: 50 },    // Steady: 3분 동안 50명 유지
    { duration: '1m', target: 100 },   // Peak: 1분 동안 100명까지 증가
    { duration: '2m', target: 100 },   // Peak 유지: 2분 동안 100명 유지
    { duration: '30s', target: 0 },    // Ramp-down: 30초 동안 0명으로 감소
  ],
  thresholds: {
    'http_req_duration{name:PerformanceDetail}': ['p(95)<500'],  // 공연 상세 p95 < 500ms
    'http_req_duration{name:Setlist}': ['p(95)<500'],            // 셋리스트 p95 < 500ms
    'http_req_duration{name:TrackDetail}': ['p(95)<300'],        // 트랙 상세 p95 < 300ms
    http_req_duration: ['p(99)<1000'],                            // 전체 p99 < 1000ms
    http_req_failed: ['rate<0.01'],                               // 에러율 1% 미만
    errors: ['rate<0.05'],                                        // 커스텀 에러율 5% 미만
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081/api';

// 테스트 데이터 (실제 존재하는 ID로 변경 필요)
const PERFORMANCE_ID = 1;
const TRACK_ID = 1;

export default function () {
  // 1. 공연 상세 조회 테스트
  testPerformanceDetail();
  sleep(1);

  // 2. 셋리스트 조회 테스트
  testSetlist();
  sleep(1);

  // 3. 트랙 상세 조회 테스트
  testTrackDetail();
  sleep(1);
}

function testPerformanceDetail() {
  const response = http.get(`${BASE_URL}/performances/${PERFORMANCE_ID}`, {
    tags: { name: 'PerformanceDetail' },
  });

  const success = check(response, {
    'PerformanceDetail - status is 200': (r) => r.status === 200,
    'PerformanceDetail - response has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.isSuccess === true && body.data !== null;
      } catch (e) {
        return false;
      }
    },
    'PerformanceDetail - response time < 500ms': (r) => r.timings.duration < 500,
  });

  errorRate.add(!success);
  performanceDetailTrend.add(response.timings.duration);
}

function testSetlist() {
  const response = http.get(`${BASE_URL}/performances/${PERFORMANCE_ID}/setlist`, {
    tags: { name: 'Setlist' },
  });

  const success = check(response, {
    'Setlist - status is 200': (r) => r.status === 200,
    'Setlist - response has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.isSuccess === true && body.data !== null;
      } catch (e) {
        return false;
      }
    },
    'Setlist - response time < 500ms': (r) => r.timings.duration < 500,
  });

  errorRate.add(!success);
  setlistTrend.add(response.timings.duration);
}

function testTrackDetail() {
  const response = http.get(`${BASE_URL}/tracks/${TRACK_ID}`, {
    tags: { name: 'TrackDetail' },
  });

  const success = check(response, {
    'TrackDetail - status is 200': (r) => r.status === 200,
    'TrackDetail - response has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.isSuccess === true && body.data !== null;
      } catch (e) {
        return false;
      }
    },
    'TrackDetail - response time < 300ms': (r) => r.timings.duration < 300,
  });

  errorRate.add(!success);
  trackDetailTrend.add(response.timings.duration);
}

// 테스트 종료 후 요약 출력
export function handleSummary(data) {
  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    'k6-results/summary.json': JSON.stringify(data),
  };
}
