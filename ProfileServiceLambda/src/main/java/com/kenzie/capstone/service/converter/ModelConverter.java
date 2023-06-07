package com.kenzie.capstone.service.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.capstone.service.model.profile.service.Profile;
import com.kenzie.capstone.service.model.ProfileEntity;

public class ModelConverter {

    public static ProfileEntity convertToEntity(Profile profile) {
        return new ProfileEntity(
                profile.getUserId(),
                profile.getName(),
                profile.getEmail(),
                profile.getCreatedDate(),
                profile.getLastUpdated(),
                profile.getDiets()
        );
    }

    public static Profile convertFromEntity(ProfileEntity entity) {
        return new Profile(
                entity.getUserId(),
                entity.getName(),
                entity.getEmail(),
                entity.getCreatedDate(),
                entity.getLastUpdated(),
                entity.getDiets()
        );
    }

    public static <T> String toJson(T object) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, classOfT);
    }

}
