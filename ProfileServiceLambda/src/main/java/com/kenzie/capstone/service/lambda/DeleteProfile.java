package com.kenzie.capstone.service.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.capstone.service.ProfileService;
import com.kenzie.capstone.service.dependency.DaggerServiceComponent;
import com.kenzie.capstone.service.exception.ProfileNotFoundException;
import com.kenzie.capstone.service.model.profile.service.Profile;

public class DeleteProfile implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String userId = input.getPathParameters().get("userId");
        ProfileService profileService = DaggerServiceComponent.create().provideProfileService();
        try {
            Profile profile = profileService.deleteProfile(userId);
            String profileJson = gson.toJson(profile);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(profileJson);
        } catch (ProfileNotFoundException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(404)
                    .withBody(e.getMessage());
        }
    }

}
