import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// 스파이크 테스트 설정 - 급격한 트래픽 증가 시나리오
export const options = {
  stages: [
    { duration: '10s', target: 10 },    // 정상 트래픽 (10명)
    { duration: '30s', target: 500 },   // 급격한 증가 - 스파이크 (500명)
    { duration: '10s', target: 10 },    // 정상으로 복귀
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'],  // 스파이크 시 더 여유있는 threshold (1초)
    http_req_failed: ['rate<0.1'],      // 에러율 10% 미만
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081/api';

export default function () {
  // 1~10 범위의 랜덤 공연 ID로 테스트
  const performanceId = Math.floor(Math.random() * 10) + 1;

  const response = http.get(`${BASE_URL}/performances/${performanceId}`, {
    tags: { name: 'SpikeTest' },
  });

  check(response, {
    'status is 200 or 404': (r) => r.status === 200 || r.status === 404,
    'response time acceptable': (r) => r.timings.duration < 2000,
  });

  sleep(0.5);
}

// 테스트 종료 후 요약 출력
export function handleSummary(data) {
  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    'k6-results/spike-test-summary.json': JSON.stringify(data),
  };
}
