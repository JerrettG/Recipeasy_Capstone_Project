package com.kenzie.capstone.service.dao;

import com.kenzie.capstone.service.caching.CacheClient;
import com.kenzie.capstone.service.converter.ModelConverter;
import com.kenzie.capstone.service.exception.ProfileNotFoundException;
import com.kenzie.capstone.service.model.ProfileEntity;


import java.util.Optional;

public class CachingProfileDao {

    private final CacheClient cacheClient;
    private final NonCachingProfileDao nonCachingProfileDao;

    private final static String PROFILE_CACHE_KEY = "Profile::%s";
    private final static int PROFILE_CACHE_TTL = 60 * 60;

    public CachingProfileDao(CacheClient cacheClient, NonCachingProfileDao nonCachingProfileDao) {
        this.cacheClient = cacheClient;
        this.nonCachingProfileDao = nonCachingProfileDao;
    }

    /**
     * Retrieves the profile with specified userId from the cache. If cache request is a miss, retrieves profile
     * from the database.
     * @param userId The userId of the profile to be retrieved.
     * @return The profile with the specified userId.
     * @throws ProfileNotFoundException if profile with userId does not exist.
     */
    public ProfileEntity getProfile(String userId) throws ProfileNotFoundException {
        String cacheKey = String.format(PROFILE_CACHE_KEY, userId);
        Optional<String> cachedProfile = cacheClient.getValue(cacheKey);

        if (cachedProfile.isPresent()) {
            // If the profile is in the cache, return it
            return ModelConverter.fromJson(cachedProfile.get(), ProfileEntity.class);
        }

        // Otherwise, retrieve the profile entity from the DAO based on the userId
        ProfileEntity profileEntity = nonCachingProfileDao.getProfile(userId);

        // If the profile entity is null, throw an exception indicating that it could not be found
        if (profileEntity == null) {
            throw new ProfileNotFoundException("Profile not found for userId: " + userId);
        }

        // Cache the profile for future requests
        addToCache(cacheKey, profileEntity);

        return profileEntity;
    }

    /**
     * Takes the profileEntity and persists the entity in the database using the nonCachingProfileDao.
     * @param profileEntity The entity to be persisted.
     */
    public void createProfile(ProfileEntity profileEntity) {
        nonCachingProfileDao.createProfile(profileEntity);
    }

    /**
     * Updates the persisted entity in the database. Invalidates the cache of the entity.
     * @param profileEntity The entity that's updated information will be persisted in the database.
     */
    public void updateProfile(ProfileEntity profileEntity) {
        String userId = profileEntity.getUserId();
        // Create key for cacheClient
        String key = String.format(PROFILE_CACHE_KEY, userId);
        // delete old profile from cacheClient
        cacheClient.invalidate(key);
        // Update profile in database
        nonCachingProfileDao.updateProfile(profileEntity);
    }

    /**
     * Deletes the entity from the database.
     * @param profileEntity
     */
    public void deleteProfile(ProfileEntity profileEntity) {
        String userId = profileEntity.getUserId();
        // Create key for cacheClient
        String key = String.format(PROFILE_CACHE_KEY, userId);
        // delete old profile from cacheClient
        cacheClient.invalidate(key);
        // Delete profile from database
        nonCachingProfileDao.deleteProfile(profileEntity);


    }

    /**
     * Adds the passed in entity into the cache for future retrieval via the passed in key.
     * @param key Key that will be used for future retrievals of the passed in entity from the cache.
     * @param profileEntity The entity that will be cached for future retrieval.
     */
    private void addToCache(String key, ProfileEntity profileEntity) {
        // Cache for One Hour
        cacheClient.setValue(key, PROFILE_CACHE_TTL, ModelConverter.toJson(profileEntity));
    }

}
