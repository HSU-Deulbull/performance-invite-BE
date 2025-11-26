# 성능 최적화 가이드 - Redis 캐싱 & K6 테스트

이 문서는 Deulbull 프로젝트에 적용된 Redis 캐싱과 K6 부하 테스트를 설명합니다.

📌 캐싱 적용된 API 목록

1. 공연 상세 조회 ✅

- 엔드포인트: GET /api/performances/{performanceId}
- 캐시 이름: performanceDetail
- TTL: 30분
- 캐시 키: performanceId
- 적용 위치: PerformanceServiceImpl.getPerformanceDetail() (PerformanceServiceImpl.java:174)

응답 내용: 공연 정보, 밴드 정보, 현재 재생 중인 곡, 이미지, 외부 링크 등

  ---
2. 공연 셋리스트 조회 ✅

- 엔드포인트: GET /api/performances/{performanceId}/setlist
- 캐시 이름: performanceSetlist
- TTL: 30분
- 캐시 키: performanceId
- 적용 위치: PerformanceServiceImpl.getPerformanceSetlist() (PerformanceServiceImpl.java:240)

응답 내용: 공연의 전체 곡 목록 (순서대로 정렬됨)

  ---
3. 트랙 상세 조회 ✅

- 엔드포인트: GET /api/tracks/{performanceSongId}
- 캐시 이름: trackDetail
- TTL: 1시간 (가장 긴 TTL - 수정 빈도가 낮음)
- 캐시 키: performanceSongId
- 적용 위치: PerformanceSongsServiceImpl.getPerformanceSongsDetail() (PerformanceSongsServiceImpl.java:30)

---

## 목차
1. [Redis 캐싱 적용](#1-redis-캐싱-적용)
2. [K6 부하 테스트](#2-k6-부하-테스트)
3. [빠른 시작 가이드](#3-빠른-시작-가이드)

---

## 1. Redis 캐싱 적용

### 1.1 캐싱이 적용된 API

다음 조회 API들에 Redis 캐싱이 적용되었습니다:

| API | 엔드포인트 | 캐시 이름 | TTL | 설명 |
|-----|----------|---------|-----|------|
| 공연 상세 조회 | `GET /api/performances/{id}` | `performanceDetail` | 30분 | 공연 정보, 밴드, 현재 재생곡 |
| 셋리스트 조회 | `GET /api/performances/{id}/setlist` | `performanceSetlist` | 30분 | 공연의 전체 곡 목록 |
| 트랙 상세 조회 | `GET /api/tracks/{id}` | `trackDetail` | 1시간 | 개별 곡 상세 정보 |

### 1.2 캐시 무효화 전략

캐시는 다음 상황에서 자동으로 무효화됩니다:

- **트랙 좋아요 기능** (`@CacheEvict`): 좋아요 수가 변경되면 해당 트랙 캐시 삭제
- **수동 삭제**: Redis CLI에서 `FLUSHALL` 또는 `DEL` 명령어 사용

### 1.3 구현 상세

#### RedisConfig.java
```java
@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // JDK 직렬화 사용 (안정적)
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

        // 기본 캐시 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jdkSerializer))
                .entryTtl(Duration.ofMinutes(30))  // 기본 TTL: 30분
                .disableCachingNullValues();

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("performanceDetail", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("performanceSetlist", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("trackDetail", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
```

#### 서비스 레이어 적용 예시
```java
@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "performanceDetail", key = "#performanceId")
    public PerformanceDetailResponseDto getPerformanceDetail(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);
        return PerformanceDetailResponseDto.from(performance);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "performanceSetlist", key = "#performanceId")
    public PerformanceSetlistResponse getPerformanceSetlist(Long performanceId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(PerformanceNotFoundException::new);
        return PerformanceSetlistResponse.from(performance);
    }
}
```

```java
@Service
@RequiredArgsConstructor
public class PerformanceSongsServiceImpl implements PerformanceSongsService {

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "trackDetail", key = "#performanceSongId")
    public PerformanceSongsDetailResponseDto getPerformanceSongsDetail(Long performanceSongId) {
        PerformanceSong ps = performanceSongsRepository.findWithSongById(performanceSongId)
                .orElseThrow(PerformanceSongsNotFoundException::new);
        // ... 변환 로직
    }

    @Override
    @Transactional
    @CacheEvict(value = "trackDetail", key = "#performanceSongId")
    public PerformanceSongsLikeResponseDto getPerformanceSongsLike(
            Long performanceSongId,
            PerformanceSongsLikeRequestDto liked) {
        // 좋아요 처리 후 캐시 무효화
        int delta = liked.liked() ? 1 : -1;
        performanceSongsRepository.likes(performanceSongId, delta);
        // ...
    }
}
```

### 1.4 Redis 실행 방법

Windows에서 Docker를 사용하여 Redis를 실행합니다.

#### 전제 조건
- Docker Desktop 설치 필수 (자세한 내용은 `REDIS_WINDOWS_GUIDE.md` 참고)

#### Redis 시작
```bash
# 프로젝트 루트 폴더에서
start-redis.bat
```

#### Redis CLI 접속
```bash
docker exec -it redis-deulbull redis-cli

# 연결 테스트
ping
# 응답: PONG

# 캐시 조회
KEYS *
GET "performanceDetail::1"

# 캐시 삭제
FLUSHALL
```

---

## 2. K6 부하 테스트

### 2.1 K6 설치

#### Windows
```bash
# Chocolatey 사용
choco install k6

# 설치 확인
k6 version
```

#### macOS
```bash
brew install k6
```

### 2.2 테스트 종류

프로젝트에는 3가지 K6 테스트 스크립트가 포함되어 있습니다:

| 테스트 | 파일명 | 용도 | 소요 시간 |
|--------|--------|------|-----------|
| Smoke Test | `smoke-test.js` | 기본 기능 동작 확인 | 30초 |
| Performance Test | `performance-api-test.js` | 실제 부하 테스트 | 약 8분 |
| Spike Test | `spike-test.js` | 급격한 트래픽 증가 테스트 | 50초 |

### 2.3 Smoke Test (기본 기능 확인)

**목적**: 캐싱이 적용된 3개 API가 정상 동작하는지 확인

**설정**:
- 가상 사용자: 1명
- 지속 시간: 30초
- Threshold: p95 < 500ms, 에러율 < 1%

**실행 방법**:
```bash
cd k6-tests
k6 run smoke-test.js
```

**테스트 대상 API**:
1. `GET /api/performances/1` - 공연 상세 조회
2. `GET /api/performances/1/setlist` - 셋리스트 조회
3. `GET /api/tracks/1` - 트랙 상세 조회

**성공 조건**:
- ✅ 모든 API가 200 OK 응답
- ✅ 응답 본문이 유효한 JSON 형식
- ✅ `isSuccess: true` 포함

**실행 결과 예시**:
```
✓ GET /performances/:id - status is 200
✓ GET /performances/:id - has valid response
✓ GET /performances/:id/setlist - status is 200
✓ GET /performances/:id/setlist - has valid response
✓ GET /tracks/:id - status is 200
✓ GET /tracks/:id - has valid response

checks.........................: 100.00% ✓ 30   ✗ 0
http_req_duration..............: avg=45.23ms  min=12.34ms med=38.56ms max=123.45ms p(95)=98.76ms
http_req_failed................: 0.00%   ✓ 0    ✗ 30
```

### 2.4 Performance Test (부하 테스트)

**목적**: 실제 운영 환경을 시뮬레이션하여 성능 측정

**부하 단계**:
1. Warm-up: 30초 동안 0 → 10명
2. Ramp-up: 1분 동안 10 → 50명
3. Steady: 3분 동안 50명 유지
4. Peak: 1분 동안 50 → 100명
5. Peak 유지: 2분 동안 100명 유지
6. Ramp-down: 30초 동안 100 → 0명

**Thresholds**:
- 공연 상세 p95 < 500ms
- 셋리스트 p95 < 500ms
- 트랙 상세 p95 < 300ms
- 전체 p99 < 1000ms
- 에러율 < 1%

**실행 방법**:
```bash
cd k6-tests
k6 run performance-api-test.js
```

**성공 조건**:
```
✓ PerformanceDetail - status is 200
✓ PerformanceDetail - response has data
✓ PerformanceDetail - response time < 500ms
✓ Setlist - status is 200
✓ Setlist - response has data
✓ Setlist - response time < 500ms
✓ TrackDetail - status is 200
✓ TrackDetail - response has data
✓ TrackDetail - response time < 300ms

http_req_duration{name:PerformanceDetail}: avg=XXms p(95)=XXms ✓
http_req_duration{name:Setlist}:          avg=XXms p(95)=XXms ✓
http_req_duration{name:TrackDetail}:      avg=XXms p(95)=XXms ✓
```

### 2.5 Spike Test (급격한 트래픽 테스트)

**목적**: 갑작스러운 트래픽 급증 시 시스템 안정성 확인

**부하 단계**:
1. 정상: 10초 동안 10명 유지
2. 스파이크: 30초 동안 10 → 500명 급증
3. 복귀: 10초 동안 500 → 10명 감소

**실행 방법**:
```bash
cd k6-tests
k6 run spike-test.js
```

---

## 3. 빠른 시작 가이드

캐싱 적용 및 성능 테스트를 빠르게 시작하는 방법입니다.

### 3.1 전체 프로세스

```bash
# 1. Redis 시작
start-redis.bat

# 2. 애플리케이션 실행
gradlew.bat bootRun

# 3. 새 터미널에서 Smoke Test 실행 (30초)
cd k6-tests
k6 run smoke-test.js

# 4. Smoke Test 통과 후 Performance Test 실행 (약 8분)
k6 run performance-api-test.js
```

### 3.2 테스트 전 체크리스트

- [ ] Docker Desktop이 실행 중인가?
- [ ] Redis 컨테이너가 실행 중인가? (`docker ps`)
- [ ] Spring Boot 애플리케이션이 실행 중인가? (포트 8081)
- [ ] DB에 테스트 데이터가 존재하는가? (performanceId=1, trackId=1)
- [ ] K6가 설치되어 있는가? (`k6 version`)

### 3.3 성능 측정 방법

#### 캐싱 효과 비교

**1단계: 캐시 없이 측정 (Redis 중지)**
```bash
docker stop redis-deulbull
gradlew.bat bootRun
k6 run smoke-test.js
# 결과 기록: p95, avg
```

**2단계: 캐시 적용 측정 (Redis 시작)**
```bash
start-redis.bat
gradlew.bat bootRun
k6 run smoke-test.js
# 결과 기록: p95, avg
```

**3단계: 개선율 계산**
```
개선율 = (Before - After) / Before × 100%
```

**예상 결과**:
- 첫 요청 (Cache Miss): DB 조회, 느림
- 두 번째 요청부터 (Cache Hit): Redis 조회, 빠름
- p95 응답 시간: 30-50% 개선
- 처리량(RPS): 2-3배 증가

### 3.4 Redis 캐시 모니터링

#### 실시간 캐시 확인
```bash
# 1. Redis CLI 접속
docker exec -it redis-deulbull redis-cli

# 2. 모든 키 조회
KEYS *
# 출력 예:
# 1) "performanceDetail::1"
# 2) "performanceSetlist::1"
# 3) "trackDetail::1"

# 3. 특정 캐시 조회 (값은 직렬화되어 있어 읽기 어려움)
GET "performanceDetail::1"

# 4. TTL 확인 (남은 유효 시간, 초 단위)
TTL "performanceDetail::1"
# 출력 예: 1785 (약 30분 = 1800초)
```

#### 캐시 히트/미스 로깅

`src/main/resources/application-dev.yml`:
```yaml
logging:
  level:
    org.springframework.cache: DEBUG
    org.springframework.data.redis: DEBUG
```

로그 출력 예시:
```
# Cache Miss (DB 조회)
Cache miss for key 'performanceDetail::1'
Hibernate: select ... from performance

# Cache Hit (Redis 조회)
Cache hit for key 'performanceDetail::1'
```

---

## 4. 문제 해결

### 4.1 K6 테스트 실패

**증상**: `http_req_failed: 100%`, 모든 요청 실패

**해결**:
```bash
# 1. 애플리케이션 실행 확인
curl http://localhost:8081/api/performances/1

# 2. 포트 확인
netstat -an | findstr 8081

# 3. DB 데이터 확인
# MySQL에서 performanceId=1, trackId=1 데이터 존재 여부 확인
```

### 4.2 Threshold 실패

**증상**: `✗ http_req_duration{name:PerformanceDetail}: ['p(95)<500']`

**원인**: Redis가 실행되지 않아 매번 DB 조회

**해결**:
```bash
# Redis 실행 확인
docker ps --filter "name=redis-deulbull"

# 없으면 시작
start-redis.bat
```

### 4.3 캐시가 적용되지 않음

**증상**: Redis는 실행 중이지만 성능 개선 없음

**확인 사항**:
1. `@EnableCaching`이 `RedisConfig`에 있는가?
2. `@Cacheable` 어노테이션이 서비스 메서드에 있는가?
3. DTO가 `Serializable`을 구현했는가?
4. `application-dev.yml`에 Redis 설정이 있는가?

**디버깅**:
```bash
# Redis에 데이터가 저장되는지 확인
docker exec -it redis-deulbull redis-cli
KEYS *
# 비어있으면 캐싱 미적용

# Spring Boot 로그 확인
# "Lettuce ConnectionFactory initialized" 메시지 확인
```

---

## 5. 추가 정보

### 5.1 관련 파일

| 파일 | 설명 |
|------|------|
| `REDIS_WINDOWS_GUIDE.md` | Windows에서 Redis 설치 및 실행 가이드 |
| `start-redis.bat` | Redis Docker 컨테이너 시작 스크립트 |
| `stop-redis.bat` | Redis 중지 스크립트 |
| `check-docker.bat` | Docker 실행 상태 확인 스크립트 |
| `k6-tests/smoke-test.js` | 기본 기능 확인 테스트 |
| `k6-tests/performance-api-test.js` | 부하 테스트 |
| `k6-tests/spike-test.js` | 스파이크 테스트 |
| `src/main/java/.../config/RedisConfig.java` | Redis 캐시 설정 |

### 5.2 참고 자료

- [K6 공식 문서](https://k6.io/docs/)
- [Spring Cache 문서](https://docs.spring.io/spring-framework/reference/integration/cache.html)
- [Redis 공식 문서](https://redis.io/docs/)
- [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)

### 5.3 성능 개선 팁

1. **TTL 조정**: 데이터 수정 빈도에 따라 TTL 조정
   - 자주 변경: 10-15분
   - 가끔 변경: 30-60분
   - 거의 변경 없음: 1-2시간

2. **캐시 워밍**: 애플리케이션 시작 시 자주 사용되는 데이터 미리 로드

3. **직렬화 방식**:
   - JDK 직렬화: 안정적이지만 용량 큼
   - JSON 직렬화: 가독성 좋고 용량 작지만 LocalDateTime 등 추가 설정 필요

4. **모니터링**:
   - Redis CLI로 주기적으로 `KEYS *` 확인
   - TTL 만료 전 데이터 갱신 고려
   - 메모리 사용량 모니터링 (`INFO memory`)
