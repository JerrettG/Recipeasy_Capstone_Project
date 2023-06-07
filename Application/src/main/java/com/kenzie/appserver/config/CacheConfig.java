package com.kenzie.appserver.config;

import com.kenzie.appserver.caching.InMemoryCacheStore;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;


import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {


    @Bean
    public InMemoryCacheStore inMemoryCacheStore() {
        return new InMemoryCacheStore(120, TimeUnit.SECONDS, 1000);
    }

    @Bean("JedisPoolConfig")
    public JedisPoolConfig jedisPoolConfig() {
        return new JedisPoolConfig();
    }
}
