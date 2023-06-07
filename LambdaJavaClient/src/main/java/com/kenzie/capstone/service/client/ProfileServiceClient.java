package com.kenzie.capstone.service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.capstone.service.model.profile.service.Profile;


public class ProfileServiceClient {
    private static final String GET_PROFILE_ENDPOINT = "api/v1/profileService/profile/{userId}";
    private static final String CREATE_PROFILE_ENDPOINT = "api/v1/profileService/profile";
    private static final String UPDATE_PROFILE_ENDPOINT = "api/v1/profileService/profile";
    private static final String DELETE_PROFILE_ENDPOINT = "api/v1/profileService/profile/{userId}";

    private ObjectMapper mapper;

    public ProfileServiceClient() {
        this.mapper = new ObjectMapper();
    }


    public Profile getProfile(String userId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(GET_PROFILE_ENDPOINT.replace("{userId}", userId));
        Profile profile;
        try {
            profile = mapper.readValue(response, Profile.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return profile;
    }

    public Profile createProfile(String data) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.postEndpoint(CREATE_PROFILE_ENDPOINT, data);
        Profile profile;
        try {
            profile = mapper.readValue(response, Profile.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return profile;
    }

    public Profile updateProfile(String data) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.putEndpoint(UPDATE_PROFILE_ENDPOINT, data);
        Profile profile;
        try {
            profile = mapper.readValue(response, Profile.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return profile;
    }

    public Profile deleteProfile(String userId) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.deleteEndpoint(DELETE_PROFILE_ENDPOINT.replace("{userId}", userId));
        Profile profile;
        try {
            profile = mapper.readValue(response, Profile.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e);
        }
        return profile;
    }
}
