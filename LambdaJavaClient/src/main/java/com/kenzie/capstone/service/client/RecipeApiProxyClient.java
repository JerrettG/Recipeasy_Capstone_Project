package com.kenzie.capstone.service.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyIngredientResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyRecipeResponse;
import com.kenzie.capstone.service.model.recipe.service.IngredientResults;
import com.kenzie.capstone.service.model.recipe.service.RecipeResults;

import java.util.List;
import java.util.Map;

public class RecipeApiProxyClient {
    private static final String PROXY_API_BASE_PATH = "proxy/v1";
    private static final String GET_RECIPES_BY_COMPLEX_SEARCH_ENDPOINT = "/recipes/complexSearch";
    private static final String GET_INGREDIENTS_BY_SEARCH_ENDPOINT = "/ingredients/search";
    private static final String GET_RECIPE_BY_ID_ENDPOINT = "/recipes/%s";

    private ObjectMapper mapper;

    public RecipeApiProxyClient() {
        this.mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Sends requests to the RecipeApiProxy "complexSearch" endpoint which is a proxy for the third-party API
     * @param queryParams A map of query parameters to be sent a part of the request to the proxy
     * @return a List of Recipes
     * @throws ApiGatewayException if the response cannot be mapped to RecipeResults
     */
    public List<ProxyRecipeResponse> getRecipesByComplexSearch(Map<String, String> queryParams) {
        EndpointUtility endpointUtility = new EndpointUtility();

        String response = endpointUtility.getEndpoint(PROXY_API_BASE_PATH + GET_RECIPES_BY_COMPLEX_SEARCH_ENDPOINT, queryParams);

        try {
            return mapper.readValue(response, RecipeResults.class).getRecipes();
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e, 400);
        }
    }

    /**
     * Sends a request to the RecipeApiProxy getRecipeById endpoint which is a proxy for the third-party API
     * @param id The id of the recipe you want to retrieve
     * @return The retrieved Recipe.
     * @throws ApiGatewayException if the response cannot be mapped to ProxyRecipeResponse
     */
    public ProxyRecipeResponse getRecipeById(String id) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String endpoint = String.format(PROXY_API_BASE_PATH + GET_RECIPE_BY_ID_ENDPOINT, id);
        String response = endpointUtility.getEndpoint(endpoint);

        try {
            return mapper.readValue(response, ProxyRecipeResponse.class);
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e, 400);
        }
    }

    public List<ProxyIngredientResponse> getIngredients(Map<String, String> queryParams) {
        EndpointUtility endpointUtility = new EndpointUtility();
        String response = endpointUtility.getEndpoint(PROXY_API_BASE_PATH + GET_INGREDIENTS_BY_SEARCH_ENDPOINT, queryParams);

        try {
            return mapper.readValue(response, IngredientResults.class).getIngredients();
        } catch (Exception e) {
            throw new ApiGatewayException("Unable to map deserialize JSON: " + e, 400);
        }
    }
}
