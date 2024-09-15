package com.flab.weshare.config.cacheConfig;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisCacheConfig {
	@Value("${cache.ttl_minutes}")
	private int cacheDuartionMinutes;

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
		Map<String, RedisCacheConfiguration> cacheConfigMap = Arrays.stream(RedisCacheInfos.values())
			.collect(Collectors.toMap(
				RedisCacheInfos::getCacheName,
				redisCache -> RedisCacheConfiguration.defaultCacheConfig()
					.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
						new Jackson2JsonRedisSerializer(objectMapper, redisCache.getClazz())))
					.disableCachingNullValues()
					.entryTtl(redisCache.getExpiredAfterWrite())
					.prefixCacheNameWith("weshare:")
			));

		return RedisCacheManager.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.withInitialCacheConfigurations(cacheConfigMap)
			.build();
	}
}
