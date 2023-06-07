package com.kenzie.appserver.service.model;

import java.util.List;

public class Recipe {

    private final Integer id;

    private final String name;
    private final List<Ingredient> ingredients;
    private final Integer readyInMinutes;
    private final List<Instruction> instructions;
    private final String imageUrl;
    private final String imageSourceUrl;
    private final String summary;


    public Recipe(Integer id, String name, List<Ingredient> ingredients, Integer readyInMinutes, List<Instruction> instructions, String imageUrl, String imageSourceUrl, String summary) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.readyInMinutes = readyInMinutes;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.imageSourceUrl = imageSourceUrl;
        this.summary = summary;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageSourceUrl() {
        return imageSourceUrl;
    }

    public String getSummary() {
        return summary;
    }
}
