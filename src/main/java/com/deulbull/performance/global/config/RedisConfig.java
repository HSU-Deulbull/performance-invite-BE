package com.deulbull.performance.global.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // JDK 기본 직렬화 사용 (가장 안정적)
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

        // 기본 캐시 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(jdkSerializer)
                )
                .entryTtl(Duration.ofMinutes(30))  // 기본 TTL: 30분
                .disableCachingNullValues();

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 공연 상세 - 30분
        cacheConfigurations.put("performanceDetail",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 셋리스트 - 30분
        cacheConfigurations.put("performanceSetlist",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 트랙 상세 - 1시간 (수정 빈도가 낮음)
        cacheConfigurations.put("trackDetail",
                defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}