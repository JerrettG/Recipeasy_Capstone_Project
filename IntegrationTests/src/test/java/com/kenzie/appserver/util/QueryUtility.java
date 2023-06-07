package com.kenzie.appserver.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kenzie.appserver.controller.model.*;
import com.kenzie.capstone.service.model.profile.service.Profile;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

public class QueryUtility {
    public RecipeServiceClient recipeServiceClient;
    public ProfileServiceClient profileServiceClient;

    private final MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String RECIPE_SERVICE_BASE_PATH = "/api/v1/recipeService";
    private final String PROFILE_SERVICE_BASE_PATH = "/api/v1/profileService";

    public QueryUtility(MockMvc mvc) {
        this.mvc = mvc;
        this.recipeServiceClient = new RecipeServiceClient();
        this.profileServiceClient = new ProfileServiceClient();
    }

    public class RecipeServiceClient {
        public ResultActions getRecipes(Map<String, String> queryParameters) throws Exception {
            StringBuilder url = new StringBuilder(RECIPE_SERVICE_BASE_PATH + "/recipes?");
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                url.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            return mvc.perform(get(url.toString())
                    .accept(MediaType.APPLICATION_JSON));
        }
        public ResultActions getIngredients(Map<String, String> queryParameters) throws Exception {
            StringBuilder url = new StringBuilder(RECIPE_SERVICE_BASE_PATH + "/ingredients?");
            for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
                url.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
            return mvc.perform(get(url.toString())
                    .accept(MediaType.APPLICATION_JSON));
        }
        public ResultActions getRecipeById(String id) throws Exception {
            return mvc.perform(get(RECIPE_SERVICE_BASE_PATH + "/recipes/{id}", id)
                    .accept(MediaType.APPLICATION_JSON));
        }

        public ResultActions getAllFridgeItemsByUserId(String userId) throws Exception {
            return mvc.perform(get(RECIPE_SERVICE_BASE_PATH + "/fridgeItems/{userId}", userId)
                    .accept(MediaType.APPLICATION_JSON));
        }

        public ResultActions getRecipesUsingFridgeItems(String userId, boolean maximizeIngredients) throws Exception {
            return mvc.perform(
                    get(RECIPE_SERVICE_BASE_PATH + "/recipes/fridgeItems/{userId}?maximizeIngredients={maximizeIngredients}",
                            userId,
                            maximizeIngredients)
                            .accept(MediaType.APPLICATION_JSON));
        }
        public ResultActions updateFridgeItem(UpdateFridgeItemRequest request) throws Exception {
            return mvc.perform(put(RECIPE_SERVICE_BASE_PATH + "/fridgeItems")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)));
        }
        public ResultActions removeFridgeItem(RemoveFridgeItemRequest removeFridgeItemRequest) throws Exception {
            return mvc.perform(delete(RECIPE_SERVICE_BASE_PATH + "/fridgeItems")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(removeFridgeItemRequest)));
        }

        public ResultActions addFridgeItem(AddFridgeItemRequest addFridgeItemRequest) throws Exception {
            return mvc.perform(post(RECIPE_SERVICE_BASE_PATH + "/fridgeItems")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(addFridgeItemRequest)));
        }

        public ResultActions getSavedRecipesByUserId(String userId) throws Exception {
            return mvc.perform(get(RECIPE_SERVICE_BASE_PATH + "/savedRecipes/{userId}", userId)
                    .accept(MediaType.APPLICATION_JSON));
        }

        public ResultActions getSavedRecipeForUserByRecipeId(String userId, Integer recipeId) throws Exception {
            return mvc.perform(get(RECIPE_SERVICE_BASE_PATH + "/savedRecipes/{userId}/{recipeId}", userId, recipeId)
                    .accept(MediaType.APPLICATION_JSON));
        }

        public ResultActions addSavedRecipe(AddSavedRecipeRequest request) throws Exception {
            return mvc.perform(post(RECIPE_SERVICE_BASE_PATH + "/savedRecipes")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)));
        }

        public ResultActions removeSavedRecipe(RemoveSavedRecipeRequest request) throws Exception {
            return mvc.perform(delete(RECIPE_SERVICE_BASE_PATH + "/savedRecipes")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request)));
        }

    }
    public class ProfileServiceClient {
        public ResultActions getProfile(String userId) throws Exception{
            return mvc.perform(
                    get(PROFILE_SERVICE_BASE_PATH + "/profile/{userId}", userId)
                            .accept(MediaType.APPLICATION_JSON)
            );
        }

        public ResultActions createProfile(String userId, String name, String email, List<Diet> diets) throws Exception {
            Profile profile = new Profile();
            profile.setUserId(userId);
            profile.setName(name);
            profile.setEmail(email);
            profile.setDiets(new ArrayList<>(diets));
            return mvc.perform(
                    post(PROFILE_SERVICE_BASE_PATH + "/profile")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(profile))
            );
        }
        public ResultActions updateProfile(String userId, String name, String email, List<Diet> diets) throws Exception {
            Profile profile = new Profile();
            profile.setUserId(userId);
            profile.setName(name);
            profile.setEmail(email);
            profile.setDiets(new ArrayList<>(diets));
            return mvc.perform(put(PROFILE_SERVICE_BASE_PATH + "/profile")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(profile))
            );
        }

        public ResultActions deleteProfile(String userId) throws Exception{
            return mvc.perform(delete(PROFILE_SERVICE_BASE_PATH + "/profile/{userId}", userId)
                    .accept(MediaType.APPLICATION_JSON)
            );
        }
    }

}

