# K6 부하 테스트 스크립트

이 디렉토리는 Deulbull API의 성능을 테스트하기 위한 K6 스크립트를 포함합니다.

## 테스트 종류

### 1. smoke-test.js (스모크 테스트)
**목적**: 기본 기능이 정상 동작하는지 확인

**실행 방법**:
```bash
k6 run smoke-test.js
```

**특징**:
- 1명의 가상 사용자
- 30초 동안 실행
- 모든 주요 API 엔드포인트 테스트
- 빠른 확인용

---

### 2. performance-api-test.js (부하 테스트)
**목적**: 점진적으로 증가하는 부하에서 성능 측정

**실행 방법**:
```bash
# 로컬 환경
k6 run performance-api-test.js

# 특정 URL 지정
k6 run --env BASE_URL=http://localhost:8080/api performance-api-test.js

# 결과를 JSON으로 저장
k6 run --out json=../k6-results/results.json performance-api-test.js
```

**부하 시나리오**:
1. Warm-up: 30초 동안 10명까지 증가
2. Ramp-up: 1분 동안 50명까지 증가
3. Steady: 3분 동안 50명 유지
4. Peak: 1분 동안 100명까지 증가
5. Peak 유지: 2분 동안 100명 유지
6. Ramp-down: 30초 동안 0명으로 감소

**성능 목표**:
- 공연 상세 조회 p95 < 500ms
- 셋리스트 조회 p95 < 500ms
- 트랙 상세 조회 p95 < 300ms
- 전체 p99 < 1000ms
- 에러율 < 1%

---

### 3. spike-test.js (스파이크 테스트)
**목적**: 급격한 트래픽 증가 시 시스템 안정성 확인

**실행 방법**:
```bash
k6 run spike-test.js
```

**부하 시나리오**:
1. 정상: 10초 동안 10명
2. 스파이크: 30초 동안 500명으로 급증
3. 복귀: 10초 동안 10명으로 복귀

**목적**:
- 갑작스러운 트래픽 급증 시 시스템 반응 확인
- 자동 스케일링 테스트
- 장애 복구 시간 측정

---

## 테스트 전 준비사항

### 1. 애플리케이션 실행
```bash
# 프로젝트 루트에서
./gradlew bootRun

# 또는 Windows
gradlew.bat bootRun
```

### 2. 테스트 데이터 확인
스크립트에서 사용하는 ID가 실제 DB에 존재하는지 확인:
- `PERFORMANCE_ID = 1`
- `TRACK_ID = 1`

필요시 스크립트 파일에서 ID 수정

### 3. Redis 실행 (캐싱 테스트 시)
```bash
# Docker 사용
docker run -d --name redis-deulbull -p 6379:6379 redis:7-alpine

# WSL 사용
sudo service redis-server start
```

---

## 결과 해석

### 주요 메트릭

- **http_req_duration**: 전체 요청 응답 시간
  - `avg`: 평균
  - `p(95)`: 95% 요청이 이 시간 내 완료
  - `p(99)`: 99% 요청이 이 시간 내 완료

- **http_req_failed**: 실패한 요청 비율

- **http_reqs**: 초당 처리한 요청 수 (RPS)

- **checks**: 테스트 통과율

### 예시 결과
```
✓ status is 200
✓ response has data
✓ response time < 500ms

checks.........................: 98.50% ✓ 2955  ✗ 45
http_req_duration..............: avg=234ms p(95)=456ms p(99)=789ms
http_req_failed................: 0.50%
http_reqs......................: 32.26/s
vus............................: 50 min=10 max=100
```

---

## 캐싱 효과 비교

### 1. 캐싱 전 테스트
```bash
# Redis 없이 실행
./gradlew bootRun

# 테스트 실행 및 결과 저장
k6 run --out json=../k6-results/before-cache.json performance-api-test.js
```

### 2. 캐싱 후 테스트
```bash
# Redis 시작
docker run -d --name redis-deulbull -p 6379:6379 redis:7-alpine

# 애플리케이션 재시작
./gradlew bootRun

# 테스트 실행 및 결과 저장
k6 run --out json=../k6-results/after-cache.json performance-api-test.js
```

### 3. 비교 분석
- p95 응답시간 개선율
- RPS (초당 요청 처리량) 증가율
- 에러율 감소

---

## 트러블슈팅

### Connection refused 에러
```
애플리케이션이 실행 중인지 확인:
- Windows: netstat -an | findstr 8080
- Mac/Linux: lsof -i :8080
```

### 404 에러
```
테스트 데이터(ID)가 존재하는지 확인:
- 브라우저에서 http://localhost:8080/api/performances/1 접속
- 또는 스크립트의 ID 값 수정
```

### Threshold 초과
```
성능 목표를 달성하지 못한 경우:
1. 데이터베이스 쿼리 최적화
2. Redis 캐싱 적용
3. 인덱스 추가
4. N+1 쿼리 문제 해결
```

---

## 추가 옵션

### 더 많은 부하
```bash
k6 run --vus 200 --duration 10m performance-api-test.js
```

### 특정 시나리오만 실행
smoke-test.js를 수정하여 특정 API만 테스트

### HTML 리포트 생성 (확장 기능)
```bash
# k6-reporter 설치 후
k6 run --out json=results.json performance-api-test.js
k6-reporter results.json
```
