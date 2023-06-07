package com.kenzie.appserver.caching;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.kenzie.appserver.service.model.FridgeItem;
import com.kenzie.appserver.service.model.SavedRecipe;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class InMemoryCacheStore {

    private final Cache<String, List<FridgeItem>> userIdToFridgeItemsCache;
    private final Cache<String, List<SavedRecipe>> userIdToSavedRecipesCache;

    public InMemoryCacheStore(int expiry, TimeUnit timeUnit, long maximumSize) {
        this.userIdToFridgeItemsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiry, timeUnit)
                .maximumSize(maximumSize)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors()) //uses the available CPU cores for max concurrency
                .build();
        this.userIdToSavedRecipesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiry, timeUnit)
                .maximumSize(maximumSize)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors()) //uses the available CPU cores for max concurrency
                .build();
    }

    public Optional<List<FridgeItem>> getFridgeItemsForUser(String key) {
        checkNonNull(key);
        return Optional.ofNullable(userIdToFridgeItemsCache.getIfPresent(key));
    }

    public void invalidateFridgeItemsForUser(String key) {
        checkNonNull(key);
        userIdToFridgeItemsCache.invalidate(key);
    }

    public void setFridgeItemsForUser(String key, List<FridgeItem> fridgeItems) {
        checkNonNull(key);
        userIdToFridgeItemsCache.put(key, fridgeItems);
    }
    public Optional<List<SavedRecipe>> getSavedRecipesForUser(String key) {
        checkNonNull(key);
        return Optional.ofNullable(userIdToSavedRecipesCache.getIfPresent(key));
    }

    public void invalidateSavedRecipesForUser(String key) {
        checkNonNull(key);
        userIdToSavedRecipesCache.invalidate(key);
    }

    public void setSavedRecipesForUser(String key, List<SavedRecipe> savedRecipes) {
        checkNonNull(key);
        userIdToSavedRecipesCache.put(key, savedRecipes);
    }

    private void checkNonNull(String key) {
        Optional.ofNullable(key).orElseThrow(() -> new IllegalArgumentException("Cache cannot accept null key"));
    }

}
