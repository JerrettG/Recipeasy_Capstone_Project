package com.kenzie.appserver.controller;

import com.amazonaws.Response;
import com.kenzie.appserver.controller.model.*;
import com.kenzie.appserver.exceptions.FridgeItemAlreadyExistsException;
import com.kenzie.appserver.exceptions.FridgeItemNotFoundException;
import com.kenzie.appserver.exceptions.SavedRecipeAlreadyExistsException;
import com.kenzie.appserver.exceptions.SavedRecipeNotFoundException;
import com.kenzie.appserver.service.RecipeService;
import com.kenzie.appserver.service.model.FridgeItem;
import com.kenzie.appserver.service.model.Ingredient;
import com.kenzie.appserver.service.model.Recipe;
import com.kenzie.appserver.service.model.SavedRecipe;
import com.kenzie.appserver.util.ModelConverter;
import com.kenzie.capstone.service.client.ApiGatewayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recipeService")
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<RecipeResponse>> getRecipes(HttpServletRequest request) {
        Map<String, String> queryParams = getRequestParams(request);
        try {
            List<Recipe> recipes = recipeService.getRecipes(queryParams);
            List<RecipeResponse> recipeResponses = recipes.stream()
                    .map(ModelConverter::convertToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(recipeResponses);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
    @GetMapping("/recipes/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable("id") String id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id);
            RecipeResponse response = ModelConverter.convertToResponse(recipe);
            return ResponseEntity.ok(response);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/ingredients")
    public ResponseEntity<List<IngredientResponse>> getIngredients(HttpServletRequest request) {
        Map<String, String> queryParams = getRequestParams(request);
        try {
            List<Ingredient> ingredients = recipeService.getIngredients(queryParams);
            List<IngredientResponse> ingredientResponses = ingredients.stream()
                    .map(ModelConverter::convertToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ingredientResponses);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }


    @GetMapping("/recipes/fridgeItems/{userId}")
    public ResponseEntity<List<RecipeResponse>> getRecipesUsingFridgeItems(
            @PathVariable("userId") String userId,
            @RequestParam(name = "maximizeIngredients", defaultValue = "true") boolean maximizeIngredients) {
        try {
            List<Recipe> recipes = recipeService.getRecipesUsingFridgeItems(userId, maximizeIngredients);
            List<RecipeResponse> response = recipes.stream()
                    .map(ModelConverter::convertToResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (ApiGatewayException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (ExecutionException e) {
            return ResponseEntity.notFound().build();
        } catch (InterruptedException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/fridgeItems/{userId}")
    public ResponseEntity<List<FridgeItemResponse>> getAllFridgeItemsByUserId(@PathVariable("userId") String userId) {
        // Retrieve the customer's fridge items for the given userId from the RecipeService
        List<FridgeItem> fridgeItems = recipeService.getAllFridgeItemsByUserId(userId);
        List<FridgeItemResponse> response = fridgeItems.stream()
                .map(ModelConverter::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/fridgeItems")
    public ResponseEntity<FridgeItemResponse> addFridgeItem(
            @RequestBody AddFridgeItemRequest addFridgeItemRequest) {
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setName(addFridgeItemRequest.getName());
        fridgeItem.setUserId(addFridgeItemRequest.getUserId());
        fridgeItem.setQuantity(addFridgeItemRequest.getQuantity());
        fridgeItem.setPurchaseDate(addFridgeItemRequest.getPurchaseDate());
        fridgeItem.setUnitMeasurement(addFridgeItemRequest.getUnitMeasurement());
        fridgeItem.setImageFileName(addFridgeItemRequest.getImageFileName());

        try {
            //Storing the fridgeItem
            FridgeItem addedFridgeItem = recipeService.addFridgeItem(fridgeItem);

            FridgeItemResponse response = ModelConverter.convertToResponse(addedFridgeItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (FridgeItemAlreadyExistsException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/fridgeItems")
    public ResponseEntity<FridgeItemResponse> updateFridgeItem(@RequestBody UpdateFridgeItemRequest request) {
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(request.getUserId());
        fridgeItem.setName(request.getName());
        fridgeItem.setPurchaseDate(request.getPurchaseDate());
        fridgeItem.setQuantity(request.getQuantity());
        fridgeItem.setUnitMeasurement(request.getUnitMeasurement());
        fridgeItem.setImageFileName(request.getImageFileName());

        try {
            FridgeItem updatedItem = recipeService.updateFridgeItem(fridgeItem);
            FridgeItemResponse response = ModelConverter.convertToResponse(updatedItem);
            return ResponseEntity.ok(response);
        } catch (FridgeItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/fridgeItems")
    public ResponseEntity<FridgeItemResponse> removeFridgeItem(
            @RequestBody RemoveFridgeItemRequest request) {
        FridgeItem fridgeItem = new FridgeItem();
        fridgeItem.setUserId(request.getUserId());
        fridgeItem.setName(request.getName());
        try {
            FridgeItem item = recipeService.removeFridgeItem(fridgeItem);
            FridgeItemResponse response = ModelConverter.convertToResponse(item);
            return ResponseEntity.ok(response);
        } catch (FridgeItemNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/savedRecipes/{userId}")
    public ResponseEntity<List<SavedRecipeResponse>> getSavedRecipesByUserId(@PathVariable("userId") String userId) {
        try {
            // Retrieve the customer's saved recipes for the given userId from the RecipeService
            List<SavedRecipe> savedRecipes = recipeService.getSavedRecipesByUserId(userId);
            List<SavedRecipeResponse> response = savedRecipes.stream()
                    .map(ModelConverter::convertToResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/savedRecipes/{userId}/{recipeId}")
    public ResponseEntity<SavedRecipeResponse> getSavedRecipeForUserByRecipeId(
            @PathVariable("userId") String userId,
            @PathVariable("recipeId") Integer recipeId) {
        try {
            SavedRecipe savedRecipe = recipeService.getSavedRecipeForUserByRecipeId(userId, recipeId);
            SavedRecipeResponse response = ModelConverter.convertToResponse(savedRecipe);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SavedRecipeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    
    @PostMapping("/savedRecipes")
    public ResponseEntity<SavedRecipeResponse> addSavedRecipesByUserId(@RequestBody AddSavedRecipeRequest request) {
        // Convert AddSavedRecipeRequest to SavedRecipe and save recipe
        SavedRecipe saveRecipe = new SavedRecipe();
        saveRecipe.setUserId(request.getUserId());
        saveRecipe.setRecipeId(request.getRecipeId());
        saveRecipe.setName(request.getName());
        saveRecipe.setIngredients(request.getIngredients());
        saveRecipe.setReadyInMinutes(request.getReadyInMinutes());
        saveRecipe.setInstructions(request.getInstructions());
        saveRecipe.setImageUrl(request.getImageUrl());
        saveRecipe.setImageSourceUrl(request.getImageSourceUrl());
        saveRecipe.setSummary(request.getSummary());

        try {
            SavedRecipe addedRecipe = recipeService.addSavedRecipe(saveRecipe);

            // Convert SavedRecipe to a SavedRecipeResponse object
            SavedRecipeResponse savedRecipeResponse = ModelConverter.convertToResponse(addedRecipe);

            // Return SavedRecipeResponse in body w/ HTTP status 201
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipeResponse);
        } catch (IllegalArgumentException | SavedRecipeAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/savedRecipes")
    public ResponseEntity<SavedRecipeResponse> removeSavedRecipe(
            @RequestBody RemoveSavedRecipeRequest request) {
        SavedRecipe savedRecipe = new SavedRecipe();
        savedRecipe.setUserId(request.getUserId());
        savedRecipe.setRecipeId(request.getRecipeId());
        try {
            SavedRecipe item = recipeService.removeSavedRecipe(savedRecipe);
            SavedRecipeResponse response = ModelConverter.convertToResponse(item);
            return ResponseEntity.ok(response);
        } catch (SavedRecipeNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String[]> requestParameterMap = request.getParameterMap();
        // Pass the request parameters to the RecipeService to retrieve the recipes
        Map<String, String> queryParams = new HashMap<>();
        for (Map.Entry<String, String[]> entry : requestParameterMap.entrySet()) {
            queryParams.put(entry.getKey(), entry.getValue()[0]);
        }
        return queryParams;
    }
}
