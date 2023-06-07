package com.kenzie.capstone.recipe.api.proxy.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.kenzie.capstone.recipe.api.proxy.RecipeApiProxy;
import com.kenzie.capstone.recipe.api.proxy.dependency.DaggerProxyComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Optional;

public class GetRecipeById implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        RecipeApiProxy recipeApiProxy = DaggerProxyComponent.create().provideRecipeApiProxy();
        HttpResponse<String> apiResponse;
        String recipeId = input.getPathParameters().get("id");
        try {
            apiResponse = recipeApiProxy.getRecipeById(recipeId);

            return response
                    .withStatusCode(apiResponse.statusCode())
                    .withBody(apiResponse.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            return response
                    .withStatusCode(400)
                    .withBody(e.getMessage());
        }

//        return response.withStatusCode(200)
//                .withBody(getMockResponseBody());
    }

    private String getMockResponseBody() {
        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mockResponseDataSingleItem.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            return reader.readLine();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}