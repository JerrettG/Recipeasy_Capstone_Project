package com.kenzie.appserver.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kenzie.appserver.controller.model.*;
import com.kenzie.appserver.repositories.model.*;
import com.kenzie.appserver.service.model.*;
import com.kenzie.capstone.service.model.recipe.proxy.Diet;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyIngredientResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyInstructionResponse;
import com.kenzie.capstone.service.model.recipe.proxy.ProxyRecipeResponse;

import java.util.*;
import java.util.stream.Collectors;

public class ModelConverter {


    public static String convertFridgeItemsToCsvString(List<FridgeItem> fridgeItems) {
        StringBuilder csvBuilder = fridgeItems.stream()
                .reduce(
                        new StringBuilder(),
                        (sb, fridgeItem) -> sb
                                .append(fridgeItem.getName())
                                .append(','),
                        StringBuilder::append);
        if (csvBuilder.length() > 0) {
            csvBuilder.deleteCharAt(csvBuilder.length()-1); // remove the trailing comma
        }
        return csvBuilder.toString();
    }

    public static String convertDietsToCsvString(List<Diet> diets) {
        StringBuilder csvBuilder = diets.stream()
                .reduce(
                        new StringBuilder(),
                        (sb, diet) -> sb
                                .append(diet.getDiet())
                                .append(','),
                        StringBuilder::append);
        if (csvBuilder.length() > 0) {
            csvBuilder.deleteCharAt(csvBuilder.length()-1); // remove the trailing comma
        }
        return csvBuilder.toString();
    }

    public static <T> String toJson(T object) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, classOfT);
    }
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, typeToken.getType());
    }

    public static RecipeResponse convertToResponse(Recipe recipe){
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                Optional.ofNullable(recipe.getIngredients())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ModelConverter::convertToResponse)
                        .collect(Collectors.toList()),
                recipe.getReadyInMinutes(),
                Optional.ofNullable(recipe.getInstructions())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(ModelConverter::convertToResponse)
                        .collect(Collectors.toList()),
                recipe.getImageUrl(),
                recipe.getImageSourceUrl(),
                recipe.getSummary());
    }

    public static FridgeItemResponse convertToResponse(FridgeItem fridgeItem) {
        return new FridgeItemResponse(
                fridgeItem.getUserId(),
                fridgeItem.getName(),
                fridgeItem.getPurchaseDate(),
                fridgeItem.getLastUpdated(),
                fridgeItem.getQuantity(),
                fridgeItem.getUnitMeasurement(),
                fridgeItem.getImageFileName()
        );
    }

    public static IngredientResponse convertToResponse(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getQuantity(),
                ingredient.getUnitMeasurement(),
                ingredient.getImageFileName());
    }

    public static InstructionResponse convertToResponse(Instruction instruction) {
        return new InstructionResponse(
                instruction.getNumber(),
                instruction.getStep());
    }
    public static SavedRecipeResponse convertToResponse(SavedRecipe savedRecipe){
        return new SavedRecipeResponse(
                savedRecipe.getUserId(),
                savedRecipe.getRecipeId(),
                savedRecipe.getName(),
                savedRecipe.getIngredients(),
                savedRecipe.getReadyInMinutes(),
                savedRecipe.getInstructions(),
                savedRecipe.getImageUrl(),
                savedRecipe.getImageSourceUrl(),
                savedRecipe.getSummary()
        );
    }

    public static FridgeItemEntity itemToEntity(FridgeItem fridgeItem){
        return new FridgeItemEntity(
                fridgeItem.getUserId(),
                fridgeItem.getName(),
                fridgeItem.getPurchaseDate(),
                fridgeItem.getLastUpdated(),
                fridgeItem.getQuantity(),
                fridgeItem.getUnitMeasurement(),
                fridgeItem.getImageFileName()
                );
    }

    /**
     Convert a ProxyRecipeResponse object to a Recipe object.
     @param proxyRecipeResponse the ProxyRecipeResponse object to convert
     @return a Recipe object with properties from the given proxy recipe response and transformed instructions and ingredients Lists
     */
    public static Recipe convertProxyRecipeToRecipe(ProxyRecipeResponse proxyRecipeResponse) {
        // Use a stream to transform the list of proxy instructions to a list of ProxyInstruction objects
        List<Instruction> instructions = Optional.ofNullable(proxyRecipeResponse.getInstructions())
                .orElse(Collections.emptyList())
                .stream()
                // Map each proxy instruction to an ProxyInstruction object using the "convertProxyInstruction2Instruction" method
                .map(ModelConverter::convertProxyInstructionToInstruction)
                // Collect the mapped ProxyInstruction objects into a List
                .collect(Collectors.toList());

        // Use a stream to transform the list of proxy ingredients to a list of ProxyIngredientResponse objects
        List<Ingredient> ingredients = Optional.ofNullable(proxyRecipeResponse.getIngredients())
                .orElse(Collections.emptyList())
                .stream()
                // Map each proxy ingredient to an ProxyIngredientResponse object using
                // the "convertProxyIngredient2Ingredient" method
                .map(ModelConverter::convertProxyIngredientToIngredient)
                // Collect the mapped ProxyIngredientResponse objects into a List
                .collect(Collectors.toList());

        // Return a new Recipe object with the properties from the proxy recipe response and the instructions and
        // ingredient list Lists that were just created
        return new Recipe(proxyRecipeResponse.getId(), proxyRecipeResponse.getName(),
                ingredients, proxyRecipeResponse.getReadyInMinutes(),
                instructions, proxyRecipeResponse.getImageUrl(), proxyRecipeResponse.getImageSourceUrl(),
                proxyRecipeResponse.getSummary());
    }

    /**
     Convert a ProxyInstructionResponse object to an ProxyInstruction object.
     @param proxyInstruction the ProxyInstructionResponse object to convert
     @return an ProxyInstruction object with the properties from the proxy instruction response
     */
    public static Instruction convertProxyInstructionToInstruction(ProxyInstructionResponse proxyInstruction) {
        // Return a new ProxyInstruction object with the properties from the proxy instruction response
        return new Instruction(proxyInstruction.getNumber(),
                proxyInstruction.getStep());
    }

    /**
     Convert a ProxyIngredientResponse object to an ProxyIngredientResponse object.
     @param proxyIngredient the ProxyIngredientResponse object to convert
     @return an ProxyIngredientResponse object with the properties from the proxy ingredient response
     */
    public static Ingredient convertProxyIngredientToIngredient(ProxyIngredientResponse proxyIngredient) {
        // Return a new ProxyIngredientResponse object with the properties from the proxy ingredient response
        return new Ingredient(
                proxyIngredient.getId(),
                proxyIngredient.getName(),
                proxyIngredient.getQuantity(),
                proxyIngredient.getUnitMeasurement(),
                proxyIngredient.getImageFileName());
    }

    public static Ingredient convertIngredientEntityToIngredient(IngredientEntity entity) {
        // Return an Ingredient object with the properties from the ingredient entity
        return new Ingredient(
                entity.getId(),
                entity.getName(),
                entity.getQuantity(),
                entity.getUnitMeasurement(),
                entity.getImageFileName());

    }

    public static Instruction convertInstructionEntityToInstruction(InstructionEntity entity) {
        // Return an Instruction object with the properties from the instruction entity
        return new Instruction(entity.getNumber(),
                entity.getStep());
    }

    public static FridgeItem convertFridgeItemEntityToFridgeItem(FridgeItemEntity fridgeEntity) {

        return new FridgeItem(
                fridgeEntity.getUserId(),
                fridgeEntity.getName(),
                fridgeEntity.getPurchaseDate(),
                fridgeEntity.getLastUpdated(),
                fridgeEntity.getQuantity(),
                fridgeEntity.getUnitMeasurement(),
                fridgeEntity.getImageFileName());
    }

    public static SavedRecipe convertSavedRecipeEntityToSavedRecipe(SavedRecipeEntity savedRecipeEntity) {
        List<Ingredient> ingredients = Optional.ofNullable(savedRecipeEntity.getIngredients()).orElse(Collections.emptyList())
                        .stream()
                .map(ModelConverter::convertIngredientEntityToIngredient)
                .collect(Collectors.toList());
        List<Instruction> instructions = Optional.ofNullable(savedRecipeEntity.getInstructions())
                .orElse(Collections.emptyList())
                        .stream()
                .map(ModelConverter::convertInstructionEntityToInstruction)
                .collect(Collectors.toList());

        return new SavedRecipe(
                savedRecipeEntity.getUserId(),
                savedRecipeEntity.getRecipeId(),
                savedRecipeEntity.getName(),
                ingredients,
                savedRecipeEntity.getReadyInMinutes(),
                instructions,
                savedRecipeEntity.getImageUrl(),
                savedRecipeEntity.getImageSourceUrl(),
                savedRecipeEntity.getSummary()
        );
    }

    public static SavedRecipeEntity convertSavedRecipeToSavedRecipeEntity(SavedRecipe savedRecipe) {
        List<IngredientEntity> ingredients = Optional.ofNullable(savedRecipe.getIngredients())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(ingredient -> new IngredientEntity(ingredient.getId(), ingredient.getName(), ingredient.getQuantity(),
                        ingredient.getUnitMeasurement(), ingredient.getImageFileName()))
                .collect(Collectors.toList());

        List<InstructionEntity> instructionEntities = Optional.ofNullable(savedRecipe.getInstructions())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(instruction ->  new InstructionEntity(instruction.getNumber(), instruction.getStep()))
                .collect(Collectors.toList());

        SavedRecipeEntity entity = new SavedRecipeEntity(savedRecipe.getUserId(), savedRecipe.getRecipeId(),
                savedRecipe.getName(), ingredients, savedRecipe.getReadyInMinutes(), instructionEntities, savedRecipe.getImageUrl(),
                savedRecipe.getImageSourceUrl(), savedRecipe.getSummary());
        entity.setName(savedRecipe.getName());

        return entity;
    }
}
