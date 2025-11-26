@echo off
echo ================================
echo Redis 중지 스크립트
echo ================================

echo Redis 컨테이너를 중지합니다...
docker stop redis-deulbull

echo.
echo ================================
echo Redis가 중지되었습니다.
echo ================================
echo.
echo 다시 시작: docker start redis-deulbull
echo 또는: start-redis.bat 실행
echo.
pause
