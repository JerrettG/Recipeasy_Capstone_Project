package com.kenzie.capstone.service.caching;

import com.kenzie.capstone.service.dependency.DaggerServiceComponent;

import redis.clients.jedis.Jedis;

import java.util.Optional;

public class CacheClient {

    public CacheClient() {}

    /**
     * Adds the value to the distributed cache with the specified key and time-to-live.
     * @param key The key for future retrieval of the value.
     * @param seconds The time-to-live in second for the value.
     * @param value The value to be stored in the cache
     */

    public void setValue(String key , int seconds, String value){
        checkNonNullKey(key);
        try (Jedis cache = DaggerServiceComponent.create().provideJedis()) {
            cache.setex(key, seconds, value);
        }
    }

    /**
     * Retrieves the value stored in the cache with the specified key.
     * @param key The key of the value stored in the cache.
     * @return An Optional of type String containing the json representation of the stored value.
     */
    public Optional<String> getValue(String key) {
        checkNonNullKey(key);
        try (Jedis cache = DaggerServiceComponent.create().provideJedis()) {
            return Optional.ofNullable(cache.get(key));
        }
    }

    /**
     * Removes the value stored in the cache with the specified key.
     * @param key The key of the value to removed from the cache.
     * @return True if value existed in the cache for the specified key, false otherwise.
     */
    public boolean invalidate(String key) {
        checkNonNullKey(key);
        try (Jedis cache = DaggerServiceComponent.create().provideJedis()) {
            return cache.del(key) > 0;
        }
    }

    private void checkNonNullKey(String key) {
        Optional.ofNullable(key)
                .orElseThrow(() -> new IllegalArgumentException("Cache key cannot be null"));
    }
}
