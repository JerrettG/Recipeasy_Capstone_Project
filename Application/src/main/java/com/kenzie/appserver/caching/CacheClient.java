
package com.kenzie.appserver.caching;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Optional;

@Component
public class CacheClient {
    private final JedisPool jedisPool;

    @Autowired
    public CacheClient(JedisPoolConfig jedisPoolConfig, @Value("${jedis.url}") String redisUrl) {
        this.jedisPool = new JedisPool(jedisPoolConfig, redisUrl, 6379);
    }

    /**
     * Retrieves the value stored in the cache with the specified key.
     * @param key The key of the value stored in the cache.
     * @return An Optional of type String containing the json representation of the stored value.
     */
    public Optional<String> getValue(String key) {
        checkNonNull(key);
        try (Jedis jedis = jedisPool.getResource()) {
            return Optional.ofNullable(jedis.get(key));
        }
    }
    /**
     * Adds the value to the distributed cache with the specified key and time-to-live.
     * @param key The key for future retrieval of the value.
     * @param seconds The time-to-live in second for the value.
     * @param value The value to be stored in the cache
     */
    public void setValue(String key, int seconds, String value) {
        checkNonNull(key);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }
    /**
     * Removes the value stored in the cache with the specified key.
     * @param key The key of the value to removed from the cache.
     * @return True if value existed in the cache for the specified key, false otherwise.
     */
    public void invalidate(String key) {
        checkNonNull(key);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
    private void checkNonNull(String key) {
        Optional.ofNullable(key).orElseThrow(() -> new IllegalArgumentException("Cache cannot accept null key"));
    }
}