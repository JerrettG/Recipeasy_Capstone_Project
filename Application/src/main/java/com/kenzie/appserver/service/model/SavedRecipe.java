package com.kenzie.appserver.service.model;

import com.kenzie.appserver.repositories.model.IngredientEntity;
import com.kenzie.appserver.repositories.model.InstructionEntity;
import com.kenzie.appserver.repositories.model.SavedRecipeKey;

import java.util.ArrayList;
import java.util.List;

public class SavedRecipe {
    private String userId;
    private Integer recipeId;
    private String name;
    private List<Ingredient> ingredients;
    private Integer readyInMinutes;
    private List<Instruction> instructions;
    private String imageUrl;
    private String imageSourceUrl;
    private String summary;

    public SavedRecipe(){

    }

    public SavedRecipe(String userId, Integer recipeId, String name, List<Ingredient> ingredients, Integer readyInMinutes, List<Instruction> instructions, String imageUrl, String imageSourceUrl, String summary) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.readyInMinutes = readyInMinutes;
        this.instructions = new ArrayList<>(instructions);
        this.imageUrl = imageUrl;
        this.imageSourceUrl = imageSourceUrl;
        this.summary = summary;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = new ArrayList<>(ingredients);
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public List<Instruction> getInstructions() {
        return new ArrayList<>(instructions);
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = new ArrayList<>(instructions);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageSourceUrl() {
        return imageSourceUrl;
    }

    public void setImageSourceUrl(String imageSourceUrl) {
        this.imageSourceUrl = imageSourceUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
