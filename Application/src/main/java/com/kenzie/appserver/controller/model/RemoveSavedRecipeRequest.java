package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoveSavedRecipeRequest {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("recipeId")
    private Integer recipeId;

    public RemoveSavedRecipeRequest(){

    }
    public RemoveSavedRecipeRequest(String userId, Integer recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }
}
