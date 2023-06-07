package com.kenzie.capstone.service.lambda;

import com.kenzie.capstone.service.ProfileService;
import com.kenzie.capstone.service.dependency.DaggerServiceComponent;
import com.kenzie.capstone.service.exception.ProfileNotFoundException;
import com.kenzie.capstone.service.model.profile.service.Profile;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class UpdateProfile implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Create a new GsonBuilder object
        GsonBuilder builder = new GsonBuilder();

        // Use the GsonBuilder to create a new Gson object
        Gson gson = builder.create();

        // Use Dagger to create a new instance of the ProfileService
        ProfileService profileService = DaggerServiceComponent.create().provideProfileService();

        try {
            // Convert the input JSON to a Profile object
            Profile profile = gson.fromJson(input.getBody(), Profile.class);

            // Call the updateProfile method of the profileService to update the profile
            Profile newProfile = profileService.updateProfile(profile);

            // Convert the updated profile object to JSON using Gson
            String profileJson = gson.toJson(newProfile);

            // Create a new APIGatewayProxyResponseEvent with: status code 200 & the updated profile JSON in the body
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(profileJson);
        } catch (ProfileNotFoundException e) {
            // If the profile is not found, return a 404 status code with the error message in the body
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(404)
                    .withBody(e.getMessage());
        } catch (IllegalArgumentException e) {
            // If there is an IllegalArgumentException, return 400 status code with the error message in the body
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(e.getMessage());
        }
    }
}
