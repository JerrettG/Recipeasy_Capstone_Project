package com.kenzie.capstone.recipe.api.proxy;

import com.kenzie.capstone.recipe.api.proxy.exception.InvalidQueryParameterException;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import com.kenzie.capstone.service.model.recipe.proxy.SortingOption;
import com.kenzie.capstone.service.model.recipe.proxy.ValidIngredientQueryParam;
import com.kenzie.capstone.service.model.recipe.proxy.ValidRecipeQueryParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeApiProxyTest {

    @Test
    public void getRecipeById_validRecipeId_returnsRecipe() throws IOException, InterruptedException, URISyntaxException {
        int id = 716429;

        RecipeApiProxy apiProxy = new RecipeApiProxy();
        HttpResponse<String> response = apiProxy.getRecipeById(String.valueOf(id));

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        System.out.println(response.body());
    }
    @Test
    public void getRecipeById_invalidRecipeId_notFound() throws  IOException, InterruptedException, URISyntaxException {
        int id = 0;

        RecipeApiProxy apiProxy = new RecipeApiProxy();
        HttpResponse<String> response = apiProxy.getRecipeById(String.valueOf(id));

        assertEquals(404, response.statusCode());
    }

    @Test
    public void getRecipesByComplexSearch_validQueryParams_returnsList() throws IOException, InterruptedException, URISyntaxException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidRecipeQueryParam.SORT.getValidQueryParamKey(), SortingOption.CALORIES.getSortingOption());
        queryParams.put(ValidRecipeQueryParam.DIET.getValidQueryParamKey(), Diet.KETOGENIC.getDiet());
        queryParams.put(ValidRecipeQueryParam.INSTRUCTIONS_REQUIRED.getValidQueryParamKey(), "true");
        queryParams.put(ValidRecipeQueryParam.ADD_RECIPE_INFO.getValidQueryParamKey(), "true");

        RecipeApiProxy apiProxy = new RecipeApiProxy();

        HttpResponse<String> response = apiProxy.getRecipesByComplexSearch(queryParams);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body());
    }

    @Test
    public void getRecipesByComplexSearch_invalidQueryParamKey_throwsInvalidQueryParamException() throws IOException, InterruptedException, URISyntaxException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidRecipeQueryParam.SORT.getValidQueryParamKey(), SortingOption.CALORIES.getSortingOption());
        queryParams.put("invalidKey", Diet.KETOGENIC.getDiet());
        queryParams.put(ValidRecipeQueryParam.INSTRUCTIONS_REQUIRED.getValidQueryParamKey(), "true");
        queryParams.put(ValidRecipeQueryParam.ADD_RECIPE_INFO.getValidQueryParamKey(), "true");

        RecipeApiProxy apiProxy = new RecipeApiProxy();

        assertThrows(InvalidQueryParameterException.class, () -> apiProxy.getRecipesByComplexSearch(queryParams));
    }
    @Test
    public void getRecipesByComplexSearch_invalidQueryParamValue_throwsInvalidQueryParamException() throws IOException, InterruptedException, URISyntaxException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidRecipeQueryParam.SORT.getValidQueryParamKey(), SortingOption.CALORIES.getSortingOption());
        queryParams.put(ValidRecipeQueryParam.DIET.getValidQueryParamKey(), Diet.KETOGENIC.getDiet());
        queryParams.put(ValidRecipeQueryParam.INSTRUCTIONS_REQUIRED.getValidQueryParamKey(), "true");
        queryParams.put(ValidRecipeQueryParam.ADD_RECIPE_INFO.getValidQueryParamKey(), "invalidValue");

        RecipeApiProxy apiProxy = new RecipeApiProxy();

       assertThrows(InvalidQueryParameterException.class, () -> apiProxy.getRecipesByComplexSearch(queryParams));

    }


    @Test
    public void getIngredientsBySearch_validQueryParams_returnsList() throws IOException, InterruptedException, URISyntaxException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidIngredientQueryParam.QUERY.getValidQueryParamKey(), "apple");
        queryParams.put(ValidIngredientQueryParam.ADD_CHILDREN.getValidQueryParamKey(), "true");
        queryParams.put(ValidIngredientQueryParam.META_INFORMATION.getValidQueryParamKey(), "true");

        RecipeApiProxy apiProxy = new RecipeApiProxy();

        HttpResponse<String> response = apiProxy.getIngredientsBySearch(queryParams);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body());
    }

    @Test
    public void getIngredientsBySearch_invalidQueryParamKey_throwsInvalidQueryParamException() throws IOException, InterruptedException, URISyntaxException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidIngredientQueryParam.QUERY.getValidQueryParamKey(), "apple");
        queryParams.put("invalidKey", "true");
        queryParams.put(ValidIngredientQueryParam.META_INFORMATION.getValidQueryParamKey(), "true");

        RecipeApiProxy apiProxy = new RecipeApiProxy();

        assertThrows(InvalidQueryParameterException.class, () -> apiProxy.getIngredientsBySearch(queryParams));
    }
    @Test
    public void getIngredientsBySearch_invalidQueryParamValue_throwsInvalidQueryParamException() throws IOException, InterruptedException, URISyntaxException {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(ValidIngredientQueryParam.QUERY.getValidQueryParamKey(), "apple");
        queryParams.put(ValidIngredientQueryParam.ADD_CHILDREN.getValidQueryParamKey(), "invalidValue");
        queryParams.put(ValidIngredientQueryParam.META_INFORMATION.getValidQueryParamKey(), "true");

        RecipeApiProxy apiProxy = new RecipeApiProxy();

        assertThrows(InvalidQueryParameterException.class, () -> apiProxy.getIngredientsBySearch(queryParams));

    }



}
