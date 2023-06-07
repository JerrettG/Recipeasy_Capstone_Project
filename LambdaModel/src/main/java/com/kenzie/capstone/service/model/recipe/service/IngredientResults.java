package com.kenzie.capstone.service.model.recipe.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyIngredientResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyRecipeResponse;

import java.util.List;

public class IngredientResults {

    @JsonAlias({"results"})
    private List<ProxyIngredientResponse> ingredients;

    public IngredientResults() {}
    public IngredientResults(List<ProxyIngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }

    public List<ProxyIngredientResponse> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ProxyIngredientResponse> ingredients) {
        this.ingredients = ingredients;
    }



    @Override
    public String toString() {
        return "IngredientResults{" +
                "ingredients=" + ingredients +
                '}';
    }
}
