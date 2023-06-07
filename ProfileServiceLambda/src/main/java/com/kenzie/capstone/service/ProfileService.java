package com.kenzie.capstone.service;


import com.kenzie.capstone.service.caching.CacheClient;
import com.kenzie.capstone.service.dao.CachingProfileDao;
import com.kenzie.capstone.service.converter.ModelConverter;
import com.kenzie.capstone.service.dao.NonCachingProfileDao;
import com.kenzie.capstone.service.exception.ProfileAlreadyExistsException;
import com.kenzie.capstone.service.exception.ProfileNotFoundException;
import com.kenzie.capstone.service.model.ProfileEntity;

import com.kenzie.capstone.service.model.profile.service.Profile;

import javax.inject.Inject;
import java.time.LocalDateTime;

public class ProfileService {


    private final CachingProfileDao cachingProfileDao;

    @Inject
    public ProfileService(CachingProfileDao cachingProfileDao) {
        this.cachingProfileDao = cachingProfileDao;
    }

    /**
     * Retrieved profile with the specified userId.
     * @param userId The userId of the profile to be retrieved.
     * @return The profile retrieved.
     * @throws ProfileNotFoundException If the profile attempting to be retrieved does not exist.
     */
    public Profile getProfile(String userId) throws ProfileNotFoundException {
        ProfileEntity profileEntity = cachingProfileDao.getProfile(userId);

        return ModelConverter.convertFromEntity(profileEntity);
    }

    /**
     * Creates a new profile with the information from the profile.
     * @param profile The profile to be created.
     * @return The full profile that was created.
     * @throws ProfileAlreadyExistsException If profile with userId already exists
     */
    public Profile createProfile(Profile profile) throws ProfileAlreadyExistsException {
        if (profile == null)
            throw new IllegalArgumentException("Cannot create a profile from null object.");
        if (profile.getUserId() == null || profile.getUserId().isBlank())
            throw new IllegalArgumentException("Cannot create a profile with invalid userId.");
        try {
            ProfileEntity existingProfile = cachingProfileDao.getProfile(profile.getUserId());
            throw new ProfileAlreadyExistsException(String.format("Profile with userId: %s already exists.", existingProfile.getUserId()));

        } catch (ProfileNotFoundException e) {
            String createdDate = LocalDateTime.now().toString();
            profile.setCreatedDate(createdDate);
            profile.setLastUpdated(createdDate);

            cachingProfileDao.createProfile(ModelConverter.convertToEntity(profile));

            return profile;
        }

    }

    /**
     * Updates the profile with the updated fields. Any fields in the profile that are null will
     * not be updated.
     * @param profile The profile with the updated information to be updated in the database
     * @return The fully updated profile.
     * @throws ProfileNotFoundException if profile that is attempting to be updated does not exist.
     */
    public Profile updateProfile(Profile profile) throws ProfileNotFoundException {
        if (profile == null) {
            throw new IllegalArgumentException("Cannot update profile when profile is null.");
        }
        if (profile.getUserId() == null || profile.getUserId().isBlank()) {
            throw new IllegalArgumentException("Cannot update profile with invalid userId.");
        }
        // Throws ProfileNotFoundException if the profile does not exist
        Profile existingProfile = getProfile(profile.getUserId());
        existingProfile.setName(profile.getName());
        existingProfile.setEmail(profile.getEmail());
        existingProfile.setDiets(profile.getDiets());
        existingProfile.setLastUpdated(LocalDateTime.now().toString());

        // Convert the given profile object into an entity object
        ProfileEntity profileEntity = ModelConverter.convertToEntity(existingProfile);
        // Invalidates cache and updates the profile in DB by calling the non-caching profile DAO
        cachingProfileDao.updateProfile(profileEntity);

        // Return the updated profile
        return existingProfile;
    }


    /**
     * Deletes the profile with the specified userId.
     * @param userId The userId of the profile to be deleted.
     * @return The full profile that was deleted.
     * @throws ProfileNotFoundException if profile that is attempting to be deleted does not exist.
     */
    public Profile deleteProfile(String userId) throws ProfileNotFoundException {
        Profile profile = getProfile(userId);

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUserId(userId);

        cachingProfileDao.deleteProfile(profileEntity);

        return profile;

    }

}
