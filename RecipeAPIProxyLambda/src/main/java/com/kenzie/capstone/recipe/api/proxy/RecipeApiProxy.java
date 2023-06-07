package com.kenzie.capstone.recipe.api.proxy;

import com.kenzie.capstone.recipe.api.proxy.exception.InvalidQueryParameterException;
import com.kenzie.capstone.service.model.recipe.proxy.ValidIngredientQueryParam;
import com.kenzie.capstone.service.model.recipe.proxy.ValidRecipeQueryParam;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeApiProxy {

    private static final String EXTERNAL_RECIPE_API_BASE_URL = "https://api.spoonacular.com";
    private static final String EXTERNAL_RECIPE_API_COMPLEX_SEARCH_ENDPOINT = "/recipes/complexSearch";
    private static final String EXTERNAL_RECIPE_API_GET_RECIPE_BY_ID_ENDPOINT = "/recipes/%s/information";
    private static final String EXTERNAL_RECIPE_API_SEARCH_INGREDIENTS_ENDPOINT = "/food/ingredients/search";
    private static final String EXTERNAL_RECIPE_API_KEY = System.getenv("SPOONACULAR_API_KEY");


    private static final Map<String, List<String>> validRecipeQueryParams = ValidRecipeQueryParam.getAllValidQueryParams();
    private static final Map<String, List<String>> validIngredientQueryParams = ValidIngredientQueryParam.getAllValidQueryParams();

    public RecipeApiProxy() {}

    public HttpResponse<String> getRecipesByComplexSearch(Map<String, String> requestQueryParams) throws IOException, InterruptedException, URISyntaxException {
        List<NameValuePair> nameValuePairs = getNameValuePairsFromRequestParams(requestQueryParams, validRecipeQueryParams);

        return sendGet(EXTERNAL_RECIPE_API_BASE_URL + EXTERNAL_RECIPE_API_COMPLEX_SEARCH_ENDPOINT, nameValuePairs);

    }


    public HttpResponse<String> getRecipeById(String id) throws URISyntaxException, IOException, InterruptedException {
        String url = String.format(EXTERNAL_RECIPE_API_BASE_URL + EXTERNAL_RECIPE_API_GET_RECIPE_BY_ID_ENDPOINT, id);

        return sendGet(url, List.of(new BasicNameValuePair("apiKey", EXTERNAL_RECIPE_API_KEY)));
    }

    public HttpResponse<String> getIngredientsBySearch(Map<String, String> requestQueryParams) throws URISyntaxException, IOException, InterruptedException {
        List<NameValuePair> nameValuePairs = getNameValuePairsFromRequestParams(requestQueryParams, validIngredientQueryParams);

        return sendGet(EXTERNAL_RECIPE_API_BASE_URL + EXTERNAL_RECIPE_API_SEARCH_INGREDIENTS_ENDPOINT, nameValuePairs);

    }

    private HttpResponse<String> sendGet(String url, List<NameValuePair> nameValuePairs) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = new URIBuilder(URI.create(url))
                .addParameters(nameValuePairs)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<NameValuePair> getNameValuePairsFromRequestParams(Map<String, String> requestQueryParams, Map<String, List<String>> validQueryParams) {
        List<NameValuePair> nvps = new ArrayList<>();
        // add apiKey prior to map iteration because apiKey is not considered a valid query param
        nvps.add(new BasicNameValuePair("apiKey", EXTERNAL_RECIPE_API_KEY));

        for (Map.Entry<String, String> queryParam : requestQueryParams.entrySet()) {
            String key = queryParam.getKey();
            String value = queryParam.getValue();

            if (!validQueryParams.containsKey(key)) {
                throw new InvalidQueryParameterException(String.format("%s is not a valid query parameter", key));
            }
            // size 0 indicates query parameter accepts any value
            else if (validQueryParams.get(key).size() == 0 || new HashSet<>(validQueryParams.get(key)).containsAll(Arrays.stream(value.split(",")).collect(Collectors.toList()))) {
                nvps.add(new BasicNameValuePair(queryParam.getKey(), queryParam.getValue()));
            } else {
                throw new InvalidQueryParameterException(
                        String.format("%s is not a valid argument for query parameter with key %s", value, key));
            }
        }
        return nvps;
    }
}
