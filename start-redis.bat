@echo off
echo ================================
echo Redis 시작 스크립트
echo ================================

REM Redis 컨테이너가 이미 존재하는지 확인
docker ps -a --filter "name=redis-deulbull" --format "{{.Names}}" | findstr redis-deulbull >nul

if %errorlevel% equ 0 (
    echo Redis 컨테이너가 이미 존재합니다. 시작합니다...
    docker start redis-deulbull
) else (
    echo Redis 컨테이너를 새로 생성합니다...
    docker run -d --name redis-deulbull -p 6379:6379 redis:7-alpine
)

echo.
echo Redis 실행 상태:
docker ps --filter "name=redis-deulbull"

echo.
echo Redis 연결 테스트:
timeout /t 2 /nobreak >nul
docker exec redis-deulbull redis-cli ping

echo.
echo ================================
echo Redis가 성공적으로 시작되었습니다!
echo 포트: 6379
echo ================================
echo.
echo Redis CLI 접속: docker exec -it redis-deulbull redis-cli
echo Redis 중지: docker stop redis-deulbull
echo.
pause
