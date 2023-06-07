package com.kenzie.appserver.task;

import com.kenzie.capstone.service.client.ProfileServiceClient;
import com.kenzie.capstone.service.model.profile.service.Profile;

import java.util.concurrent.Callable;

public class GetProfile implements Callable<Profile> {

    private final String userId;
    private final ProfileServiceClient profileServiceClient;

    public GetProfile(String userId, ProfileServiceClient profileServiceClient) {
        this.userId = userId;
        this.profileServiceClient = profileServiceClient;
    }


    @Override
    public Profile call() {
        return profileServiceClient.getProfile(userId);
    }
}
