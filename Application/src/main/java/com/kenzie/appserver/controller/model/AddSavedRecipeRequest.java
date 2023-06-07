

package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenzie.appserver.service.model.Ingredient;
import com.kenzie.appserver.service.model.Instruction;

import java.util.List;
import java.util.Objects;

public class AddSavedRecipeRequest {

    @JsonProperty("userId")
    private String userId;
    @JsonProperty("recipeId")
    private Integer recipeId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ingredients")
    private List<Ingredient> ingredients;
    @JsonProperty("readyInMinutes")
    private Integer readyInMinutes;
    @JsonProperty("instructions")
    private List<Instruction> instructions;
    @JsonProperty("imageUrl")
    private String imageUrl;
    @JsonProperty("imageSourceUrl")
    private String imageSourceUrl;
    @JsonProperty("summary")
    private String summary;

    public AddSavedRecipeRequest() {
    }

    public AddSavedRecipeRequest(String userId, Integer recipeId, String name, List<Ingredient> ingredients, Integer readyInMinutes, List<Instruction> instructions, String imageUrl, String imageSourceUrl, String summary) {
        this.userId = userId;
        this.recipeId = recipeId;
        this.name = name;
        this.ingredients = ingredients;
        this.readyInMinutes = readyInMinutes;
        this.instructions = instructions;
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
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions) {
        this.instructions = instructions;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddSavedRecipeRequest that = (AddSavedRecipeRequest) o;
        return Objects.equals(userId, that.userId) && Objects.equals(recipeId, that.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, recipeId);
    }

    @Override
    public String toString() {
        return "AddSavedRecipeRequest{" +
                "userId='" + userId + '\'' +
                ", recipeId=" + recipeId +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", readyInMinutes=" + readyInMinutes +
                ", instructions=" + instructions +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageSourceUrl='" + imageSourceUrl + '\'' +
                ", summary='" + summary + '\'' +
                '}';
    }
}