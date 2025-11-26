import http from 'k6/http';
import { check, sleep } from 'k6';
import { textSummary } from 'https://jslib.k6.io/k6-summary/0.0.1/index.js';

// 스모크 테스트 설정 - 기본 기능 동작 확인
export const options = {
  vus: 1,              // 1명의 가상 사용자
  duration: '30s',     // 30초 동안 실행
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95%의 요청이 500ms 이하
    http_req_failed: ['rate<0.01'],    // 에러율 1% 미만
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8081/api';

export default function () {
  const PERFORMANCE_ID = 1;
  const TRACK_ID = 1;

  // 1. 공연 상세 조회
  let response = http.get(`${BASE_URL}/performances/${PERFORMANCE_ID}`);
  check(response, {
    'GET /performances/:id - status is 200': (r) => r.status === 200,
    'GET /performances/:id - has valid response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.isSuccess === true;
      } catch (e) {
        return false;
      }
    },
  });
  sleep(1);

  // 2. 셋리스트 조회
  response = http.get(`${BASE_URL}/performances/${PERFORMANCE_ID}/setlist`);
  check(response, {
    'GET /performances/:id/setlist - status is 200': (r) => r.status === 200,
    'GET /performances/:id/setlist - has valid response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.isSuccess === true;
      } catch (e) {
        return false;
      }
    },
  });
  sleep(1);

  // 3. 트랙 상세 조회
  response = http.get(`${BASE_URL}/tracks/${TRACK_ID}`);
  check(response, {
    'GET /tracks/:id - status is 200': (r) => r.status === 200,
    'GET /tracks/:id - has valid response': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.isSuccess === true;
      } catch (e) {
        return false;
      }
    },
  });
  sleep(1);
}

// 테스트 종료 후 요약 출력
export function handleSummary(data) {
  console.log('\n=== Smoke Test Summary ===');
  console.log('모든 체크가 통과하면 기본 기능이 정상 동작하는 것입니다.');

  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
    'k6-results/smoke-test-summary.json': JSON.stringify(data),
  };
}
