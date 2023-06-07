package com.kenzie.capstone.service.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.kenzie.capstone.service.model.ProfileEntity;

public class NonCachingProfileDao {

    private final DynamoDBMapper mapper;

    public NonCachingProfileDao(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public ProfileEntity getProfile(String userId) {return mapper.load(ProfileEntity.class, userId);}

    public void createProfile(ProfileEntity profileEntity) {
        mapper.save(profileEntity);
    }

    public void deleteProfile(ProfileEntity profileEntity) {
        mapper.delete(profileEntity);
    }

    public void updateProfile(ProfileEntity profileEntity) {
        mapper.save(profileEntity);
    }


}
