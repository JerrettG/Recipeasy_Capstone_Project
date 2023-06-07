package com.kenzie.capstone.recipe.api.proxy.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.kenzie.capstone.recipe.api.proxy.RecipeApiProxy;
import com.kenzie.capstone.recipe.api.proxy.exception.InvalidQueryParameterException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class GetIngredientsBySearch implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        RecipeApiProxy recipeApiProxy = new RecipeApiProxy();
        HttpResponse<String> apiResponse;
        try {
            Map<String, String> queryParams = Optional.ofNullable(input.getQueryStringParameters())
                    .orElse(Collections.emptyMap());
            apiResponse = recipeApiProxy.getIngredientsBySearch(queryParams);
        } catch (URISyntaxException | InvalidQueryParameterException | IOException | InterruptedException e) {
            return response
                    .withStatusCode(400)
                    .withBody(e.getMessage());
        }

        return response
                .withStatusCode(apiResponse.statusCode())
                .withBody(apiResponse.body());
    }
}
