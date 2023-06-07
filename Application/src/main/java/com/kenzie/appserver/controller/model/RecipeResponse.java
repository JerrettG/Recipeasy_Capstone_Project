package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenzie.appserver.service.model.Ingredient;
import com.kenzie.appserver.service.model.Instruction;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeResponse {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("ingredients")
    private List<IngredientResponse> ingredients;
    @JsonProperty("readyInMinutes")
    private Integer readyInMinutes;
    @JsonProperty("instructions")
    private List<InstructionResponse> instructions;
    @JsonProperty("imageUrl")
    private String imageUrl;
    @JsonProperty("imageSourceUrl")
    private String imageSourceUrl;
    @JsonProperty("summary")
    private String summary;
    
    public RecipeResponse(Integer id, String name, List<IngredientResponse> ingredients, Integer readyInMinutes, List<InstructionResponse> instructions, String imageUrl, String imageSourceUrl, String summary) {
        this.id = id;
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.readyInMinutes = readyInMinutes;
        this.instructions = new ArrayList<>(instructions);
        this.imageUrl = imageUrl;
        this.imageSourceUrl = imageSourceUrl;
        this.summary = summary;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IngredientResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public List<InstructionResponse> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<InstructionResponse> instructions) {
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
}
