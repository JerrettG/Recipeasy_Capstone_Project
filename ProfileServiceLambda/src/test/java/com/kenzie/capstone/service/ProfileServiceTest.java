package com.kenzie.capstone.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.capstone.service.caching.CacheClient;
import com.kenzie.capstone.service.dao.CachingProfileDao;
import com.kenzie.capstone.service.dao.NonCachingProfileDao;
import com.kenzie.capstone.service.exception.ProfileAlreadyExistsException;
import com.kenzie.capstone.service.exception.ProfileNotFoundException;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import com.kenzie.capstone.service.model.profile.service.Profile;
import com.kenzie.capstone.service.model.ProfileEntity;
import com.kenzie.capstone.service.converter.ModelConverter;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileServiceTest {


    private CachingProfileDao cachingProfileDao;
    private NonCachingProfileDao nonCachingProfileDao;
    private CacheClient cacheClient;


    private ProfileService profileService;
    private final MockNeat mockNeat = MockNeat.threadLocal();
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        cacheClient = mock(CacheClient.class);
        nonCachingProfileDao = mock(NonCachingProfileDao.class);
        cachingProfileDao = new CachingProfileDao(cacheClient, nonCachingProfileDao);
        profileService = new ProfileService(cachingProfileDao);
    }
    /*************************************
     * profileService.getProfile()       *
     ************************************/
    @Test
    public void getProfile_cacheHit_returnsProfileWhenProfileExists() throws ProfileNotFoundException, JsonProcessingException {
        // WHEN
        String userId = "user1";
        ProfileEntity profile = new ProfileEntity(
                userId,
                "Test User",
                "testuser@example.com",
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
                new ArrayList<>()
        );

        String json = objectMapper.writeValueAsString(profile);
        when(cacheClient.getValue(anyString())).thenReturn(Optional.of(json));

        // Act
        Profile result = profileService.getProfile(userId);

        // THEN
        verifyZeroInteractions(nonCachingProfileDao);
        assertEquals(profile.getUserId(), result.getUserId());
        assertEquals(profile.getName(), result.getName());
        assertEquals(profile.getEmail(), result.getEmail());
        assertEquals(profile.getCreatedDate(), result.getCreatedDate());
        assertEquals(profile.getLastUpdated(), result.getLastUpdated());
        assertEquals(profile.getDiets(), result.getDiets());
    }

    @Test
    public void getProfile_cacheMiss_returnsProfileWhenProfileExists() throws ProfileNotFoundException {
        // WHEN
        String userId = "user1";
        ProfileEntity profile = new ProfileEntity(
                userId,
                "Test User",
                "testuser@example.com",
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
                new ArrayList<>()
        );
        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());
        when(nonCachingProfileDao.getProfile(userId)).thenReturn(profile);

        // Act
        Profile result = profileService.getProfile(userId);

        // THEN
        verify(nonCachingProfileDao).getProfile(userId);
        assertEquals(profile.getUserId(), result.getUserId());
        assertEquals(profile.getName(), result.getName());
        assertEquals(profile.getEmail(), result.getEmail());
        assertEquals(profile.getCreatedDate(), result.getCreatedDate());
        assertEquals(profile.getLastUpdated(), result.getLastUpdated());
        assertEquals(profile.getDiets(), result.getDiets());
    }

    @Test
    public void getProfile_throwsExceptionWhenProfileDoesNotExist() throws ProfileNotFoundException {
        // WHEN
        String userId = "user1";
        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());
        // THEN
        Assertions.assertThrows(ProfileNotFoundException.class, () -> profileService.getProfile(userId));
    }
    /*************************************
     * profileService.createProfile()    *
     ************************************/
    @Test
    public void createProfile_validNotExistingProfile_createsProfile() throws ProfileAlreadyExistsException, ProfileNotFoundException {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();
        List<Diet> diets = new ArrayList<>();
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName(name);
        profile.setEmail(email);
        profile.setDiets(diets);

        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());

        Profile createdProfile = profileService.createProfile(profile);

        assertEquals(profile.getUserId(), createdProfile.getUserId());
        assertEquals(profile.getName(), createdProfile.getName());
        assertEquals(profile.getEmail(), createdProfile.getEmail());
        assertEquals(profile.getDiets(), createdProfile.getDiets());
        assertNotNull(createdProfile.getCreatedDate());
        assertNotNull(createdProfile.getLastUpdated());
    }

    @Test
    public void createProfile_nullProfile_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> profileService.createProfile(null));
    }
    @Test
    public void createProfile_nullUserId_throwsIllegalArgumentException() {
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();
        List<Diet> diets = new ArrayList<>();
        Profile profile = new Profile();
        profile.setUserId(null);
        profile.setName(name);
        profile.setEmail(email);
        profile.setDiets(diets);

        assertThrows(IllegalArgumentException.class, () -> profileService.createProfile(profile));
    }
    @Test
    public void createProfile_blankUserId_throwsIllegalArgumentException() {
        String userId = "";
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();
        List<Diet> diets = new ArrayList<>();
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName(name);
        profile.setEmail(email);
        profile.setDiets(diets);

        assertThrows(IllegalArgumentException.class, () -> profileService.createProfile(profile));
    }
    @Test
    public void createProfile_profileAlreadyExists_throwsProfileAlreadyExistsException() throws ProfileNotFoundException, JsonProcessingException {
        String userId = mockNeat.users().valStr();
        String name = mockNeat.names().valStr();
        String email = mockNeat.emails().valStr();
        List<Diet> diets = new ArrayList<>();
        Profile profile = new Profile();
        profile.setUserId(userId);
        profile.setName(name);
        profile.setEmail(email);
        profile.setDiets(diets);

        ProfileEntity existingProfile = new ProfileEntity();

        String jsonProfile = objectMapper.writeValueAsString(profile);

        when(cacheClient.getValue(anyString())).thenReturn(Optional.of(jsonProfile));
        assertThrows(ProfileAlreadyExistsException.class, () -> profileService.createProfile(profile));
    }
    /*************************************
     * profileService.deleteProfile()    *
     ************************************/
    @Test
    public void deleteProfile_deletesProfileWhenProfileExists() throws ProfileNotFoundException, JsonProcessingException {
        //WHEN
        String userId = "user1";
        ProfileEntity profile = new ProfileEntity(
                userId,
                "Test User",
                "testuser@example.com",
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
                new ArrayList<>()
        );
        String jsonProfile = objectMapper.writeValueAsString(profile);

        when(cacheClient.getValue(anyString())).thenReturn(Optional.of(jsonProfile));

        //Act
        Profile result = profileService.deleteProfile(userId);

        //THEN
        verify(cacheClient).invalidate(anyString());
        assertEquals(profile.getUserId(), result.getUserId());
        assertEquals(profile.getName(), result.getName());
        assertEquals(profile.getEmail(), result.getEmail());
        assertEquals(profile.getCreatedDate(), result.getCreatedDate());
        assertEquals(profile.getLastUpdated(), result.getLastUpdated());
        assertEquals(profile.getDiets(), result.getDiets());
    }
    @Test
    public void deleteProfile_throwsExceptionWhenProfileDoesNotExist() throws ProfileNotFoundException {
        //WHEN
        String userId = "user1";
        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());

        //THEN
        Assertions.assertThrows(ProfileNotFoundException.class, () -> profileService.deleteProfile(userId));
    }

    /*************************************
     * profileService.updateProfile()    *
     ************************************/
    @Test
    public void updateProfile_withValidData_returnsUpdatedProfile() throws ProfileNotFoundException, JsonProcessingException {
        // GIVEN
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUserId("123");
        profileEntity.setName("John Smith");
        profileEntity.setEmail("john.smith@example.com");
        profileEntity.setCreatedDate(LocalDateTime.now().toString());
        profileEntity.setLastUpdated(LocalDateTime.now().toString());
        List<Diet> diets = new ArrayList<>();
        diets.add(Diet.GLUTEN_FREE);
        diets.add(Diet.VEGAN);
        profileEntity.setDiets(diets);


        Profile newProfile = new Profile();
        newProfile.setUserId(profileEntity.getUserId());
        newProfile.setName(profileEntity.getName());
        newProfile.setEmail("new_johnathan.smith@example.com");
        newProfile.setDiets(profileEntity.getDiets());

        String jsonProfile = objectMapper.writeValueAsString(profileEntity);

        when(cacheClient.getValue(anyString())).thenReturn(Optional.of(jsonProfile));

        // WHEN
        Profile updatedProfile = profileService.updateProfile(newProfile);

        // THEN
        verify(cacheClient).invalidate(anyString());
        verify(nonCachingProfileDao, times(1)).updateProfile(profileEntity);
        assertEquals(profileEntity.getUserId(), updatedProfile.getUserId());
        assertEquals(profileEntity.getName(), updatedProfile.getName());
        // verify email has been updated
        assertFalse(profileEntity.getEmail().equalsIgnoreCase(newProfile.getEmail()));
        // verify lastUpdated field is not the same
        assertFalse((profileEntity.getLastUpdated().equalsIgnoreCase(updatedProfile.getLastUpdated())));
    }

    @Test
    public void updateProfile_userDoesNotExist_throwsProfileNotFoundException() throws ProfileNotFoundException {
        // GIVEN user that does not exist
        Profile doesNotExistProfile = new Profile();
        doesNotExistProfile.setUserId("doesNotExist");

        when(cacheClient.getValue(anyString())).thenReturn(Optional.empty());
        // WHEN - profileService.updateProfile method is call
        // THEN - ProfileNotFoundException is thrown
        assertThrows(ProfileNotFoundException.class, () -> profileService.updateProfile(doesNotExistProfile),
        "Expected ProfileNotFoundException to be thrown");
    }

    @Test
    public void updateProfile_nullProfile_throwsIllegalArgumentException() {
        // GIVEN :  null profile
        Profile nullProfile = null;

        // WHEN - profileService.updateProfile method is called
        // THEN - IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> profileService.updateProfile(nullProfile),
                "Expected IllegalArgumentException to be thrown");
    }

    @Test
    public void updateProfile_nullUserId_throwsIllegalArgumentException() {
        // GIVEN :  null userId
        Profile nullUserId = new Profile();
        nullUserId.setUserId(null);

        // WHEN - profileService.updateProfile method is called
        // THEN - IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> profileService.updateProfile(nullUserId),
                "Expected IllegalArgumentException to be thrown");
    }

    @Test
    public void updateProfile_blankUserId_throwsIllegalArgumentException() {
        // GIVEN blank userId
        Profile blankUserId = new Profile();
        blankUserId.setUserId("");

        // WHEN - profileService.updateProfile method is call
        // THEN - IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> profileService.updateProfile(blankUserId),
                "Expected IllegalArgumentException to be thrown");

    }





}
