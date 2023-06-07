package com.kenzie.capstone.service.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.capstone.service.ProfileService;
import com.kenzie.capstone.service.dependency.DaggerServiceComponent;
import com.kenzie.capstone.service.exception.ProfileAlreadyExistsException;
import com.kenzie.capstone.service.model.profile.service.Profile;
import org.apache.http.HttpStatus;

public class CreateProfile implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Gson gson = new GsonBuilder().create();
        Profile profileToCreate = gson.fromJson(input.getBody(), Profile.class);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        ProfileService profileService = DaggerServiceComponent.create().provideProfileService();

        try {
            Profile createdProfile = profileService.createProfile(profileToCreate);
            String jsonBody = gson.toJson(createdProfile);
            return response
                    .withStatusCode(HttpStatus.SC_CREATED)
                    .withBody(jsonBody);
        } catch (ProfileAlreadyExistsException e) {
            return response
                    .withStatusCode(400)
                    .withBody(e.getMessage());
        }
    }
}