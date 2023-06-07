package com.kenzie.capstone.service.model.recipe.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyRecipeResponse;

import java.util.List;

public class RecipeResults {

    @JsonAlias({"results"})
    private List<ProxyRecipeResponse> recipes;

    public RecipeResults() {}

    public RecipeResults(List<ProxyRecipeResponse> recipes) {
        this.recipes = recipes;
    }

    public List<ProxyRecipeResponse> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<ProxyRecipeResponse> recipes) {
        this.recipes = recipes;
    }

    @Override
    public String toString() {
        return "RecipeResults{" +
                "recipes=" + recipes +
                '}';
    }
}
